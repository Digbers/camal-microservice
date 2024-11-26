package com.microservice.inventario.service.puntoVentaI;

import com.microservice.inventario.clients.EmpresaClient;
import com.microservice.inventario.controller.DTO.AlmacenDTO;
import com.microservice.inventario.controller.DTO.EmpresaDTO;
import com.microservice.inventario.controller.DTO.PuntoVentaDTO;
import com.microservice.inventario.controller.DTO.response.DatosGeneralesResponse;
import com.microservice.inventario.persistence.entity.AlmacenEntity;
import com.microservice.inventario.persistence.entity.PuntoVentaEntity;
import com.microservice.inventario.persistence.especification.PuntoVentaSpecifications;
import com.microservice.inventario.persistence.repository.IAlmacenRepository;
import com.microservice.inventario.persistence.repository.IPuntoVentaRepository;
import com.microservice.inventario.service.almacenI.IAlmacenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PuntoVentaService implements IPuntoVentaService {
    private final IPuntoVentaRepository iPuntoVentaRepository;
    private final IAlmacenService iAlmacenService;
    private final IAlmacenRepository iAlmacenRepository;
    private final ModelMapper modelMapper;
    private final EmpresaClient empresaClient;
    @Override
    public Page<PuntoVentaDTO> findAllByEmpresa(String direccion, String nombre, Pageable pageable, Long idEmpresa) {
        try {
            Specification<PuntoVentaEntity> specification = PuntoVentaSpecifications.getPuntoVenta(direccion, nombre, idEmpresa);
            return iPuntoVentaRepository.findAll(specification, pageable).map(puntoVenta -> modelMapper.map(puntoVenta, PuntoVentaDTO.class));
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener puntoVenta por direccion y nombre: " + e.getMessage());
        }
    }

    @Override
    public Optional<PuntoVentaDTO> findById(Long id) {
        try {
            return iPuntoVentaRepository.findById(id).map(puntoVenta -> modelMapper.map(puntoVenta, PuntoVentaDTO.class));
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener puntoVenta por id : " + e.getMessage());
        }
    }

    @Override
    public PuntoVentaDTO save(PuntoVentaDTO puntoVentaDTO) {
        try {
            PuntoVentaEntity puntoVentaEntity = modelMapper.map(puntoVentaDTO, PuntoVentaEntity.class);
            AlmacenEntity almacen = iAlmacenRepository.findById(puntoVentaDTO.getAlmacen()).orElseThrow(() -> new IllegalArgumentException("No se encontro el almacen con id: " + puntoVentaDTO.getAlmacen()));
            puntoVentaEntity.setAlmacen(almacen);
            iPuntoVentaRepository.save(puntoVentaEntity);
            return modelMapper.map(puntoVentaEntity, PuntoVentaDTO.class);
        } catch (Exception e) {
            log.error("Error al guardar puntoVenta: " + e.getMessage());
            throw new RuntimeException("Error al guardar puntoVenta: " + e.getMessage());
        }
    }

    @Override
    public PuntoVentaDTO update(Long id, PuntoVentaDTO puntoVentaDTO) {
        try {
            PuntoVentaEntity puntoVentaEntity = iPuntoVentaRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("No se encontro el puntoVenta con id: " + id));
            AlmacenEntity almacen = iAlmacenRepository.findById(puntoVentaDTO.getAlmacen()).orElseThrow(() -> new IllegalArgumentException("No se encontro el almacen con id: " + puntoVentaDTO.getAlmacen()));
            puntoVentaEntity.setAlmacen(almacen);
            puntoVentaEntity.setIdEmpresa(puntoVentaDTO.getIdEmpresa());
            puntoVentaEntity.setDireccion(puntoVentaDTO.getDireccion());
            puntoVentaEntity.setNombre(puntoVentaDTO.getNombre());
            puntoVentaEntity.setUsuarioActualizacion(puntoVentaDTO.getUsuarioActualizacion());
            iPuntoVentaRepository.save(puntoVentaEntity);
            return modelMapper.map(puntoVentaEntity, PuntoVentaDTO.class);
        } catch (Exception e) {
            log.error("Error al actualizar puntoVenta: " + e.getMessage());
            throw new RuntimeException("Error al actualizar puntoVenta: " + e.getMessage());
        }
    }

    @Override
    public boolean deleteById(Long id) {
        if (iPuntoVentaRepository.findById(id).isPresent()) {
            iPuntoVentaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public List<PuntoVentaDTO> findAllByIdAlmacen(Long id) {
        try {
            return iPuntoVentaRepository.findAllByIdAlmacen(id).stream().map(puntoVenta -> modelMapper.map(puntoVenta, PuntoVentaDTO.class)).toList();
        } catch (Exception e) {
            log.error("Error al obtener puntoVenta por idAlmacen: " + e.getMessage());
            throw new RuntimeException("Error al obtener puntoVenta por idAlmacen : " + e.getMessage());
        }
    }

    @Override
    public DatosGeneralesResponse findDatosGenerales(Long idEmpresa, Long idAlmacen, Long idPuntoVenta) {
        try {
            EmpresaDTO emp = empresaClient.obtenerDetallesEmpresa(idEmpresa);
            AlmacenDTO almacen = iAlmacenService.findById(idAlmacen).get();
            PuntoVentaDTO puntoVenta = this.findById(idPuntoVenta).get();
            DatosGeneralesResponse datos = new DatosGeneralesResponse(emp.getRazonSocial(), almacen.getNombre(), puntoVenta.getNombre());
            return datos;
        } catch (Exception e) {
            log.error("Error al obtener datos generales de puntoVenta: " + e.getMessage());
            throw new RuntimeException("Error al obtener datos generales de puntoVenta : " + e.getMessage());
        }
    }
}
