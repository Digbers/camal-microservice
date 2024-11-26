package com.microservice.ventas.service.compras;

import com.microservice.ventas.controller.DTO.compras.ComprobantesComprasTiposDTO;
import com.microservice.ventas.entity.ComprobantesTiposComprasEntity;
import com.microservice.ventas.entity.especification.ComprobantesComprasTiposEspecifications;
import com.microservice.ventas.repository.IComprobantesTiposComprasRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ComprobantesComprasTiposService implements IComprobantesComprasTiposService {
    private final IComprobantesTiposComprasRepository iComprobantesTiposComprasRepository;
    private final ModelMapper modelMapper;

    @Override
    public ComprobantesComprasTiposDTO save(ComprobantesComprasTiposDTO comprobantesComprasTiposDTO) {
        try{
            ComprobantesTiposComprasEntity comprobantesTiposComprasEntity = modelMapper.map(comprobantesComprasTiposDTO, ComprobantesTiposComprasEntity.class);
            iComprobantesTiposComprasRepository.save(comprobantesTiposComprasEntity);
            return modelMapper.map(comprobantesTiposComprasEntity, ComprobantesComprasTiposDTO.class);
        } catch (Exception e) {
            log.error("Error al guardar el tipo comprobante de compra: " + e.getMessage());
            throw new RuntimeException("Error al guardar el tipo comprobante de compra: " + e.getMessage());
        }
    }

    @Override
    public ComprobantesComprasTiposDTO update(Long id, ComprobantesComprasTiposDTO comprobantesComprasTiposDTO) {
        try{
            ComprobantesTiposComprasEntity comprobantesTiposComprasEntity = iComprobantesTiposComprasRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("No se encontro el tipo comprobante de compra con id: " + id));
            comprobantesTiposComprasEntity.setCodigo(comprobantesComprasTiposDTO.getCodigo());
            comprobantesTiposComprasEntity.setDescripcion(comprobantesComprasTiposDTO.getDescripcion());
            comprobantesTiposComprasEntity.setUsuarioActualizacion(comprobantesComprasTiposDTO.getUsuarioActualizacion());
            iComprobantesTiposComprasRepository.save(comprobantesTiposComprasEntity);
            return modelMapper.map(comprobantesTiposComprasEntity, ComprobantesComprasTiposDTO.class);
        } catch (Exception e) {
            log.error("Error al actualizar el tipo comprobante de compra: " + e.getMessage());
            throw new RuntimeException("Error al actualizar el tipo comprobante de compra: " + e.getMessage());
        }
    }

    @Override
    public void deleteById(Long id) {
        try{
            ComprobantesTiposComprasEntity comprobantesTiposComprasEntity = iComprobantesTiposComprasRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("No se encontro el estado de venta con id: " + id));
            iComprobantesTiposComprasRepository.delete(comprobantesTiposComprasEntity);
        } catch (Exception e) {
            log.error("Error al eliminar el tipo comprobante de venta: " + e.getMessage());
            throw new RuntimeException("Error al eliminar el tipo comprobante de venta: " + e.getMessage());
        }
    }

    @Override
    public Page<ComprobantesComprasTiposDTO> findAllByEmpresa(String codigo, String descripcion, Pageable pageable, Long idEmpresa) {
        try{
            Specification<ComprobantesTiposComprasEntity> specification = ComprobantesComprasTiposEspecifications.getComprobantesComprasTipos(codigo, descripcion, idEmpresa);
            return iComprobantesTiposComprasRepository.findAll(specification, pageable).map(comprobantesTiposComprasEntity -> modelMapper.map(comprobantesTiposComprasEntity, ComprobantesComprasTiposDTO.class));
        } catch (Exception e) {
            log.error("Error al obtener los estados de venta: " + e.getMessage());
            throw new RuntimeException("Error al obtener los estados de venta: " + e.getMessage());
        }
    }
}
