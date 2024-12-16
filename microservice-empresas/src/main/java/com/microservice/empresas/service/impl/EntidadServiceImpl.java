package com.microservice.empresas.service.impl;

import com.microservice.empresas.controller.dto.EntidadDTO;
import com.microservice.empresas.persistence.entity.*;
import com.microservice.empresas.persistence.especification.EntidadesEspecification;
import com.microservice.empresas.persistence.repository.*;
import com.microservice.empresas.request.AsistenciaRequest;
import com.microservice.empresas.request.CreateEntidadRequest;
import com.microservice.empresas.response.EntidadResponse;
import com.microservice.empresas.response.EntidadResponseAsistencias;
import com.microservice.empresas.response.IdsEntidades;
import com.microservice.empresas.response.TrabajadoresResponse;
import com.microservice.empresas.service.IEntidadService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EntidadServiceImpl implements IEntidadService {

    private final IEntidadRepository entidadRepository;
    private final IEmpresaRepository empresaRepository;
    private final ModelMapper modelMapper;
    private final IDocumentosTiposRepository documentosTiposRepository;
    private final IEntidadesTiposRepository entidadesTiposRepository;
    private final IAsistenciasRepository asistenciasRepository;


    @Override
    public Page<EntidadDTO> findAllByEmpresa(String nombre, String apellidoPaterno, String apellidoMaterno, String documentoTipo, String nroDocumento, String email, String celular, String direccion, String sexo, Boolean estado, String condicion, Pageable pageable, Long idEmpresa) {
        try{
            Specification<EntidadEntity> specification = EntidadesEspecification.getEntidades(nombre, apellidoPaterno, apellidoMaterno, documentoTipo, nroDocumento, email, celular, direccion, sexo, estado, condicion, idEmpresa);
            return entidadRepository.findAll(specification, pageable).map(entidad -> modelMapper.map(entidad, EntidadDTO.class));
        }   catch (Exception e) {
            log.error("Error al obtener Entidades", e);
            throw new RuntimeException("Error al obtener Entidades");
        }
    }

    @Override
    public Optional<EntidadDTO> findById(Long id) {
        EntidadEntity entidadEntity = entidadRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("No se encontro la entidad con id " + id));
        return Optional.of(modelMapper.map(entidadEntity, EntidadDTO.class));
    }

    @Override
    public EntidadResponse save(CreateEntidadRequest entidadNew) {
        try {
            EntidadEntity entidad = new EntidadEntity();
            entidad.setApellidoPaterno(entidadNew.getApellidoPaterno());
            entidad.setApellidoMaterno(entidadNew.getApellidoMaterno());
            entidad.setNombre(entidadNew.getNombre());

            EmpresaEntity empresa = empresaRepository.findById(entidadNew.getIdEmpresa()).orElseThrow(() -> new EntityNotFoundException("No se encontro la empresa con id " + entidadNew.getIdEmpresa()));
            entidad.setEmpresa(empresa);
            // zona omitida
            //docu8emto tipo
            DocumentoTiposEntity documentoTipo = documentosTiposRepository.findByEmpresaAndDocCodigo(entidadNew.getIdEmpresa(), entidadNew.getDocumentoTipo()).orElseThrow(() -> new EntityNotFoundException("No se encontro el documento tipo con id " + entidadNew.getIdEmpresa()));
            entidad.setDocumentoTipo(documentoTipo);
            entidad.setNroDocumento(entidadNew.getNroDocumento());
            entidad.setEmail(entidadNew.getEmail());
            entidad.setCelular(entidadNew.getCelular());
            entidad.setDireccion(entidadNew.getDireccion());
            entidad.setSexo(entidadNew.getSexo());
            entidad.setEstado(entidadNew.getEstado());
            entidad.setCondicion(entidadNew.getCondicion());
            entidad.setSueldo(entidadNew.getSueldo());
            entidad.setUsuarioCreacion(entidadNew.getUsuarioCreacion());
            entidad.setUsuarioActualizacion(entidadNew.getUsuarioActualizacion());

            List<EntidadesTiposEntity> entidadTipos = entidadNew.getEntidadesTipos().stream()
                    .map(entidadTipoId -> {
                        // Busca la entidad tipo por empresa e identificador
                        EntidadesTiposEntity entidadTipo = entidadesTiposRepository
                                .findByEmpresaAndTipoCodigo(entidadNew.getIdEmpresa(), entidadTipoId)
                                .orElseThrow(() -> new EntityNotFoundException("No se encontró el entidad tipo con código: " + entidadTipoId));
                        return entidadTipo;
                    })
                    .collect(Collectors.toList());

            entidad.setEntidadesTiposList(entidadTipos);
            EntidadEntity entidadSaved = entidadRepository.save(entidad);
            String nombreCompleto = "";
            if(entidadNew.getDocumentoTipo().equals("DNI")){
                nombreCompleto = entidadSaved.getNombre() + " " + entidadSaved.getApellidoPaterno()+ " " + entidadSaved.getApellidoMaterno();
            }else{
                nombreCompleto = entidadSaved.getNombre();
            }
            EntidadResponse entidadResponse = EntidadResponse.builder()
                    .id(entidadSaved.getId())
                    .nombre(nombreCompleto.trim())
                    .documento(entidadSaved.getDocumentoTipo().getDocCodigo())
                    .numeroDocumento(entidadSaved.getNroDocumento())
                    .build();
            return entidadResponse;
        } catch (Exception e) {
            log.error("Error al guardar entidad: " + e.getMessage());
            throw new RuntimeException("Error al guardar entidad: " + e.getMessage());
        }
    }

    @Override
    public EntidadResponse update(Long id, CreateEntidadRequest entidadNew) {
        try {
            EntidadEntity entidad = entidadRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("No se encontro la entidad con id " + id));
            if(entidadNew.getDocumentoTipo().equals("DNI")){
                entidad.setApellidoPaterno(entidadNew.getApellidoPaterno());
                entidad.setApellidoMaterno(entidadNew.getApellidoMaterno());

            }else if(entidadNew.getDocumentoTipo().equals("RUC")){
                entidad.setNombre(entidadNew.getNombre());
                entidad.setApellidoPaterno("");
                entidad.setApellidoMaterno("");
            } else {
                entidad.setNombre(entidadNew.getNombre());
                entidad.setApellidoPaterno(entidadNew.getApellidoPaterno());
                entidad.setApellidoMaterno(entidadNew.getApellidoMaterno());
            }
            EmpresaEntity empresa = empresaRepository.findById(entidadNew.getIdEmpresa()).orElseThrow(() -> new EntityNotFoundException("No se encontro la empresa con id " + entidadNew.getIdEmpresa()));
            entidad.setEmpresa(empresa);
            // zona omitida
            DocumentoTiposEntity documentoTipo = documentosTiposRepository.findByEmpresaAndDocCodigo(entidadNew.getIdEmpresa(), entidadNew.getDocumentoTipo()).orElseThrow(() -> new EntityNotFoundException("No se encontro el documento tipo con id " + entidadNew.getIdEmpresa()));
            entidad.setDocumentoTipo(documentoTipo);
            entidad.setNroDocumento(entidadNew.getNroDocumento());
            entidad.setEmail(entidadNew.getEmail());
            entidad.setCelular(entidadNew.getCelular());
            entidad.setDireccion(entidadNew.getDireccion());
            if (entidadNew.getSexo() != null) {
                entidad.setSexo(entidadNew.getSexo());
            }
            entidad.setEstado(entidadNew.getEstado());
            if (entidadNew.getCondicion() != null) {
                entidad.setCondicion(entidadNew.getCondicion());//
            }
            if (entidadNew.getSueldo() != null) {
                entidad.setSueldo(entidadNew.getSueldo());
            }
            entidad.setUsuarioCreacion(entidadNew.getUsuarioCreacion());
            entidad.setUsuarioActualizacion(entidadNew.getUsuarioActualizacion());
            if (entidadNew.getEntidadesTipos() != null) {
                List<EntidadesTiposEntity> entidadTipos = entidadNew.getEntidadesTipos().stream()
                        .map(entidadTipoId -> {
                            // Busca la entidad tipo por empresa e identificador
                            EntidadesTiposEntity entidadTipo = entidadesTiposRepository
                                    .findByEmpresaAndTipoCodigo(entidadNew.getIdEmpresa(), entidadTipoId)
                                    .orElseThrow(() -> new EntityNotFoundException("No se encontró el entidad tipo con código: " + entidadTipoId));
                            return entidadTipo;
                        })
                        .collect(Collectors.toList());

                entidad.setEntidadesTiposList(entidadTipos);
            }
            EntidadEntity entidadSaved = entidadRepository.save(entidad);
            StringBuilder nombreCompleto = new StringBuilder();
            if(entidadNew.getDocumentoTipo().equals("DNI")){
                nombreCompleto.append(entidadSaved.getNombre()).append(" ").append(entidadSaved.getApellidoPaterno()).append(" ").append(entidadSaved.getApellidoMaterno());
            }else{
                nombreCompleto.append(entidadSaved.getNombre());
            }
            return EntidadResponse.builder()
                    .id(entidadSaved.getId())
                    .nombre(nombreCompleto.toString().trim())
                    .documento(entidadSaved.getDocumentoTipo().getDocCodigo())
                    .numeroDocumento(entidadSaved.getNroDocumento())
                    .build();
        } catch (Exception e) {
            log.error("Error al guardar entidad: " + e.getMessage());
            throw new RuntimeException("Error al guardar entidad: " + e.getMessage());
        }
    }

    @Override
    public void deleteById(Long id) {
        try {
            entidadRepository.deleteById(id);
        } catch (Exception e) {
            log.error("Error al eliminar entidad: " + e.getMessage());
            throw new RuntimeException("Error al eliminar entidad: " + e.getMessage());
        }
    }


    @Override
    public IdsEntidades search(String nombre, String nroDocumento) {
        List<Long> entidadesIds = new ArrayList<>();
        log.info("search");
        IdsEntidades idsEntidades = new IdsEntidades();
        try {
            if (nombre == null && nroDocumento == null) {
                return idsEntidades;
            }

            if (nroDocumento != null) {
                log.info("Buscando entidades por nroDocumento: " + nroDocumento);
                entidadRepository.findNroDocumento(nroDocumento).forEach(entidad -> entidadesIds.add(entidad.getId()));
            }

            if (nombre != null) {
                log.info("Buscando entidades por nombre: " + nombre);
                entidadRepository.findByNombre(nombre).forEach(entidad -> entidadesIds.add(entidad.getId()));
            }
            idsEntidades.setIds(entidadesIds);
            return idsEntidades;
        } catch (Exception e) {
            log.error("Error al buscar entidades: " + e.getMessage());
            throw new RuntimeException("Error al buscar entidades: " + e.getMessage());
        }
    }
    @Override
    public List<EntidadResponse> findEntidadesByIds(List<Long> ids) {
        List<EntidadEntity> entidades = entidadRepository.findAllById(ids);
        return transformEntidadesToEntidadResponse(entidades);
    }

    @Override
    public List<EntidadResponse> autocompleteNroDocumento(String nroDocumento) {
        List<EntidadEntity> entidades = entidadRepository.findNroDocumento(nroDocumento);
        return transformEntidadesToEntidadResponse(entidades);
    }

    @Override
    public List<EntidadResponse> autocompleteNombre(String nombre) {
        List<EntidadEntity> entidades = entidadRepository.findByNombre(nombre);
        return transformEntidadesToEntidadResponse(entidades);
    }
    private List<EntidadResponse> transformEntidadesToEntidadResponse(List<EntidadEntity> entidades) {
        return entidades.stream()
                .map(entidad -> {
                    StringBuilder nombreCompleto = new StringBuilder();
                    if(entidad.getDocumentoTipo().getDocCodigo().equals("DNI")){
                        nombreCompleto.append(entidad.getNombre()).append(" ").append(entidad.getApellidoPaterno()).append(" ").append(entidad.getApellidoMaterno());
                    }else{
                        nombreCompleto.append(entidad.getNombre());
                    }
                    EntidadResponse entidadResponse = EntidadResponse.builder()
                            .id(entidad.getId())
                            .nombre(nombreCompleto.toString().trim())
                            .documento(entidad.getDocumentoTipo().getDocCodigo())
                            .numeroDocumento(entidad.getNroDocumento())
                            .build();
                    return entidadResponse;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Page<EntidadResponseAsistencias> findWorkers(Long idEmpresa,String tipo, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        try {
            if(startDate == null || endDate == null){
                throw new RuntimeException("Error al buscar trabajadores: Fecha de inicio y fin no puede ser nula");
            }
            Page<EntidadEntity> trabajadoresPage = entidadRepository.findAllByTipoCodigoAndEmpresa(tipo, idEmpresa, startDate, endDate, pageable);
            return trabajadoresPage.map(entidad -> modelMapper.map(entidad, EntidadResponseAsistencias.class));
        } catch (Exception e) {
            log.error("Error al buscar trabajadores: " + e.getMessage());
            throw new RuntimeException("Error al buscar trabajadores: " + e.getMessage());
        }
    }

    @Override
    public Page<TrabajadoresResponse> findAllWorkers(Long idEmpresa,String tipo, Pageable pageable) {
        try{
            Page<EntidadEntity> trabajadoresPage = entidadRepository.findAllTrabajadoresByTipoCodigoAndEmpresa(tipo, idEmpresa, pageable);
            return trabajadoresPage.map(entidad -> modelMapper.map(entidad, TrabajadoresResponse.class));
        }catch (Exception e) {
            log.error("Error al buscar trabajadores: " + e.getMessage());
            throw new RuntimeException("Error al buscar trabajadores: " + e.getMessage());
        }
    }

    @Override
    public void marcarAsistencia(AsistenciaRequest asistencia) {
        try{
            EntidadEntity trabajador = entidadRepository.findById(asistencia.getIdEntidad()).orElseThrow(() -> new EntityNotFoundException("No se encontro la entidad con el id: "+ asistencia.getIdEntidad()));
            Optional<AsistenciasEntity> asistenciaExistente = asistenciasRepository.findByIdEntidadAndFechaAsistencia(trabajador.getId(), asistencia.getFechaAsistencia());
            if(asistenciaExistente.isPresent()){
                AsistenciasEntity asistenciaActual = asistenciaExistente.get();
                asistenciaActual.setAsistio(asistencia.getAsistio());
                asistenciaActual.setUsuarioActualizacion(asistencia.getUsuarioActualizacion());
                asistenciasRepository.save(asistenciaActual);
            }else {
                AsistenciasEntity asistenciaNew = new AsistenciasEntity();
                asistenciaNew.setEntidad(trabajador);
                asistenciaNew.setFechaAsistencia(asistencia.getFechaAsistencia());
                asistenciaNew.setAsistio(asistencia.getAsistio());
                asistenciaNew.setUsuarioCreacion(asistencia.getUsuarioCreacion());
                asistenciaNew.setUsuarioActualizacion(asistencia.getUsuarioActualizacion());
                asistenciasRepository.save(asistenciaNew);
            }
        } catch (Exception e) {
            log.error("Error al marcar la asisitencia: " + e.getMessage());
            throw new RuntimeException("Error al marcar la asistencia: " + e.getMessage());
        }
    }
}
