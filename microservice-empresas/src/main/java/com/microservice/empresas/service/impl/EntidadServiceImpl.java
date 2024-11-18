package com.microservice.empresas.service.impl;

import com.microservice.empresas.controller.dto.EntidadDTO;
import com.microservice.empresas.persistence.entity.DocumentoTiposEntity;
import com.microservice.empresas.persistence.entity.EmpresaEntity;
import com.microservice.empresas.persistence.entity.EntidadEntity;
import com.microservice.empresas.persistence.entity.EntidadesTiposEntity;
import com.microservice.empresas.persistence.especification.EntidadesEspecification;
import com.microservice.empresas.persistence.repository.IDocumentosTiposRepository;
import com.microservice.empresas.persistence.repository.IEmpresaRepository;
import com.microservice.empresas.persistence.repository.IEntidadRepository;
import com.microservice.empresas.persistence.repository.IEntidadesTiposRepository;
import com.microservice.empresas.request.CreateEntidadRequest;
import com.microservice.empresas.response.EntidadResponse;
import com.microservice.empresas.response.IdsEntidades;
import com.microservice.empresas.service.IEntidadService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
            if(entidadNew.getDocumentoTipo().equals("DNI")){
                entidad.setApellidoPaterno(entidadNew.getApellidoPaterno());
                entidad.setApellidoMaterno(entidadNew.getApellidoMaterno());

            }else{
                entidad.setNombre(entidadNew.getNombre());
            }
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
            String nombreCompleto = entidadSaved.getNombre() + " "
                    + entidadSaved.getApellidoPaterno() != null? entidadSaved.getApellidoPaterno() : ""
                    + " " + entidadSaved.getApellidoMaterno() != null? entidadSaved.getApellidoMaterno() : ""
                    + " " + entidadSaved.getNombre() + " " + entidadSaved.getApellidoPaterno() != null? entidadSaved.getApellidoPaterno() : "";
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
        return entidades.stream()
                .map(entidad -> {
                    EntidadResponse entidadResponse = EntidadResponse.builder()
                            .id(entidad.getId())
                            .nombre(entidad.getNombre())
                            .documento(entidad.getDocumentoTipo().getDocCodigo())
                            .numeroDocumento(entidad.getNroDocumento())
                            .build();
                    return entidadResponse;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<EntidadResponse> autocompleteNroDocumento(String nroDocumento) {
        List<EntidadEntity> entidades = entidadRepository.findNroDocumento(nroDocumento);
        return entidades.stream()
                .map(entidad -> {
                    String nombreCompleto = entidad.getNombre() + " "
                            + entidad.getApellidoPaterno() != null? entidad.getApellidoPaterno() : ""
                            + " " + entidad.getApellidoMaterno() != null? entidad.getApellidoMaterno() : ""
                            + " " + entidad.getNombre() + " " + entidad.getApellidoPaterno() != null? entidad.getApellidoPaterno() : "";
                    EntidadResponse entidadResponse = EntidadResponse.builder()
                            .id(entidad.getId())
                            .nombre(nombreCompleto.trim())
                            .documento(entidad.getDocumentoTipo().getDocCodigo())
                            .numeroDocumento(entidad.getNroDocumento())
                            .build();
                    return entidadResponse;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<EntidadResponse> autocompleteNombre(String nombre) {
        List<EntidadEntity> entidades = entidadRepository.findByNombre(nombre);
        return entidades.stream()
                .map(entidad -> {
                    String nombreCompleto = entidad.getNombre() + " "
                            + entidad.getApellidoPaterno() != null? entidad.getApellidoPaterno() : ""
                            + " " + entidad.getApellidoMaterno() != null? entidad.getApellidoMaterno() : ""
                            + " " + entidad.getNombre() + " " + entidad.getApellidoPaterno() != null? entidad.getApellidoPaterno() : "";
                    EntidadResponse entidadResponse = EntidadResponse.builder()
                            .id(entidad.getId())
                            .nombre(nombreCompleto.trim())
                            .documento(entidad.getDocumentoTipo().getDocCodigo())
                            .numeroDocumento(entidad.getNroDocumento())
                            .build();
                    return entidadResponse;
                })
                .collect(Collectors.toList());
    }
}
