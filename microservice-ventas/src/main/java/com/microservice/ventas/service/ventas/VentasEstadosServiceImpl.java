package com.microservice.ventas.service.ventas;

import com.microservice.ventas.controller.DTO.ventas.ComprobantesVentasEstadoDTO;
import com.microservice.ventas.entity.ComprobantesVentasEstadoEntity;
import com.microservice.ventas.entity.especification.ComprobantesVentasEstadosEspecification;
import com.microservice.ventas.repository.IComprobantesVentasEstadosRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VentasEstadosServiceImpl implements IVentasEstadosService {
    private final IComprobantesVentasEstadosRepository iComprobantesVentasEstadosRepository;
    private final ModelMapper modelMapper;

    @Override
    public ComprobantesVentasEstadoDTO save(ComprobantesVentasEstadoDTO comprobantesVentasEstadoDTO) {
        try {
            ComprobantesVentasEstadoEntity comprobantesVentasEstadoEntity = modelMapper.map(comprobantesVentasEstadoDTO, ComprobantesVentasEstadoEntity.class);
            iComprobantesVentasEstadosRepository.save(comprobantesVentasEstadoEntity);
            return modelMapper.map(comprobantesVentasEstadoEntity, ComprobantesVentasEstadoDTO.class);
        } catch (Exception e) {
            log.error("Error al guardar el estado de venta: " + e.getMessage());
            throw new RuntimeException("Error al guardar el estado de venta: " + e.getMessage());
        }
    }

    @Override
    public ComprobantesVentasEstadoDTO update(Long id, ComprobantesVentasEstadoDTO comprobantesVentasEstadoDTO) {
        try{
            ComprobantesVentasEstadoEntity comprobantesVentasEstadoEntity = iComprobantesVentasEstadosRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("No se encontro el estado de venta con id: " + id));
            comprobantesVentasEstadoEntity.setIdEmpresa(comprobantesVentasEstadoDTO.getIdEmpresa());
            comprobantesVentasEstadoEntity.setCodigo(comprobantesVentasEstadoDTO.getCodigo());
            comprobantesVentasEstadoEntity.setDescripcion(comprobantesVentasEstadoDTO.getDescripcion());
            comprobantesVentasEstadoEntity.setUsuarioActualizacion(comprobantesVentasEstadoDTO.getUsuarioActualizacion());
            iComprobantesVentasEstadosRepository.save(comprobantesVentasEstadoEntity);
            return modelMapper.map(comprobantesVentasEstadoEntity, ComprobantesVentasEstadoDTO.class);
        } catch (Exception e) {
            log.error("Error al actualizar el estado de venta: " + e.getMessage());
            throw new RuntimeException("Error al actualizar el estado de venta: " + e.getMessage());
        }
    }

    @Override
    public void deleteById(Long id) {
        try{
            ComprobantesVentasEstadoEntity comprobantesVentasEstadoEntity = iComprobantesVentasEstadosRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("No se encontro el estado de venta con id: " + id));
            iComprobantesVentasEstadosRepository.delete(comprobantesVentasEstadoEntity);
        } catch (Exception e) {
            log.error("Error al eliminar el estado de venta: " + e.getMessage());
            throw new RuntimeException("Error al eliminar el estado de venta: " + e.getMessage());
        }
    }

    @Override
    public List<ComprobantesVentasEstadoDTO> findByIdEmpresa(Long idEmpresa) {
        try{
            List<ComprobantesVentasEstadoEntity> comprobatesEstados = iComprobantesVentasEstadosRepository.findByIdEmpresa(idEmpresa);
            return comprobatesEstados.stream().map(comprobantesVentasEstadoEntity -> modelMapper.map(comprobantesVentasEstadoEntity, ComprobantesVentasEstadoDTO.class)).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error al obtener los estados de venta: " + e.getMessage());
            throw new RuntimeException("Error al obtener los estados de venta: " + e.getMessage());
        }
    }

    @Override
    public Page<ComprobantesVentasEstadoDTO> findAllByEmpresa(String codigo, String descripcion, Pageable pageable, Long idEmpresa) {
        try{
            Specification<ComprobantesVentasEstadoEntity> specification = ComprobantesVentasEstadosEspecification.getComprobantesVentasEstados(codigo, descripcion, idEmpresa);
            return iComprobantesVentasEstadosRepository.findAll(specification, pageable).map(comprobantesVentasEstadoEntity -> modelMapper.map(comprobantesVentasEstadoEntity, ComprobantesVentasEstadoDTO.class));
        }   catch (Exception e) {
            log.error("Error al obtener los estados de venta: " + e.getMessage());
            throw new RuntimeException("Error al obtener los estados de venta: " + e.getMessage());
        }
    }
}
