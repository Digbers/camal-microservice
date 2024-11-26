package com.microservice.ventas.service.compras;

import com.microservice.ventas.controller.DTO.compras.ComprobantesComprasEstadosDTO;
import com.microservice.ventas.entity.ComprobantesComprasEstadosEntity;
import com.microservice.ventas.entity.especification.ComprobantesComprasEstadosEspecification;
import com.microservice.ventas.repository.IComprobantesCompraEstadoRepository;
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
@Slf4j
@RequiredArgsConstructor
public class ComprasEstadosService implements IComprasEstadosService {
    private final ModelMapper modelMapper;
    private final IComprobantesCompraEstadoRepository comprobantesCompraEstadoRepository;

    @Override
    public ComprobantesComprasEstadosDTO save(ComprobantesComprasEstadosDTO comprobantesComprasEstadosDTO) {
        try{
            ComprobantesComprasEstadosEntity comprobantesComprasEstadosEntity = modelMapper.map(comprobantesComprasEstadosDTO, ComprobantesComprasEstadosEntity.class);
            comprobantesCompraEstadoRepository.save(comprobantesComprasEstadosEntity);
            return modelMapper.map(comprobantesComprasEstadosEntity, ComprobantesComprasEstadosDTO.class);
        } catch (Exception e) {
            log.error("Error al guardar el estado de compra: " + e.getMessage());
            throw new RuntimeException("Error al guardar el estado de compra: " + e.getMessage());
        }
    }

    @Override
    public ComprobantesComprasEstadosDTO update(Long id, ComprobantesComprasEstadosDTO comprobantesComprasEstadosDTO) {
        try{
            ComprobantesComprasEstadosEntity comprobantesComprasEstadosEntity = comprobantesCompraEstadoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("No se encontro el estado de compra con id: " + id));
            comprobantesComprasEstadosEntity.setIdEmpresa(comprobantesComprasEstadosDTO.getIdEmpresa());
            comprobantesComprasEstadosEntity.setCodigo(comprobantesComprasEstadosDTO.getCodigo());
            comprobantesComprasEstadosEntity.setDescripcion(comprobantesComprasEstadosDTO.getDescripcion());
            comprobantesComprasEstadosEntity.setUsuarioActualizacion(comprobantesComprasEstadosDTO.getUsuarioActualizacion());
            comprobantesCompraEstadoRepository.save(comprobantesComprasEstadosEntity);
            return modelMapper.map(comprobantesComprasEstadosEntity, ComprobantesComprasEstadosDTO.class);
        } catch (Exception e) {
            log.error("Error al actualizar el estado de compra: " + e.getMessage());
            throw new RuntimeException("Error al actualizar el estado de compra: " + e.getMessage());
        }
    }

    @Override
    public void deleteById(Long id) {
        try{
            ComprobantesComprasEstadosEntity comprobantesComprasEstadosEntity = comprobantesCompraEstadoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("No se encontro el estado de compra con id: " + id));
            comprobantesCompraEstadoRepository.delete(comprobantesComprasEstadosEntity);
        } catch (Exception e) {
            log.error("Error al eliminar el estado de compra: " + e.getMessage());
            throw new RuntimeException("Error al eliminar el estado de compra: " + e.getMessage());
        }
    }

    @Override
    public Page<ComprobantesComprasEstadosDTO> findAllByEmpresa(String codigo, String descripcion, Pageable pageable, Long idEmpresa) {
        try{
            Specification<ComprobantesComprasEstadosEntity> specification = ComprobantesComprasEstadosEspecification.getComprobantesComprasEstados(codigo, descripcion, idEmpresa);
            return comprobantesCompraEstadoRepository.findAll(specification, pageable).map(comprobantesCompraEstadoEntity -> modelMapper.map(comprobantesCompraEstadoEntity, ComprobantesComprasEstadosDTO.class));
        } catch (Exception e) {
            log.error("Error al obtener los estados de compra: " + e.getMessage());
            throw new RuntimeException("Error al obtener los estados de compra: " + e.getMessage());
        }
    }

    @Override
    public List<ComprobantesComprasEstadosDTO> findByIdEmpresa(Long idEmpresa) {
        try{
            List<ComprobantesComprasEstadosEntity> comprobatesEstados = comprobantesCompraEstadoRepository.findByIdEmpresa(idEmpresa);
            return comprobatesEstados.stream().map(comprobantesCompraEstadoEntity -> modelMapper.map(comprobantesCompraEstadoEntity, ComprobantesComprasEstadosDTO.class)).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error al obtener los estados de compra: " + e.getMessage());
            throw new RuntimeException("Error al obtener los estados de compra: " + e.getMessage());
        }
    }
}
