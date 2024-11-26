package com.microservice.ventas.service.ventas;

import com.microservice.ventas.controller.DTO.ventas.ComprobantesTiposVentasDTO;
import com.microservice.ventas.entity.ComprobantesTiposVentasEntity;
import com.microservice.ventas.entity.especification.ComprobantesVentasTiposEspecification;
import com.microservice.ventas.repository.IComprobantesTiposVentasRepository;
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
public class ComprobantesVentasTiposService implements IComprobantesVentasTiposService {
    private final IComprobantesTiposVentasRepository iComprobantesTiposVentasRepository;
    private final ModelMapper modelMapper;

    @Override
    public ComprobantesTiposVentasDTO save(ComprobantesTiposVentasDTO comprobantesTiposVentasDTO) {
        try{
            ComprobantesTiposVentasEntity comprobantesTiposVentasEntity = modelMapper.map(comprobantesTiposVentasDTO, ComprobantesTiposVentasEntity.class);
            iComprobantesTiposVentasRepository.save(comprobantesTiposVentasEntity);
            return modelMapper.map(comprobantesTiposVentasEntity, ComprobantesTiposVentasDTO.class);
        } catch (Exception e) {
            log.error("Error al guardar el tipo comprobante de venta: " + e.getMessage());
            throw new RuntimeException("Error al guardar el tipo comprobante de venta: " + e.getMessage());
        }
    }

    @Override
    public ComprobantesTiposVentasDTO update(Long id, ComprobantesTiposVentasDTO comprobantesTiposVentasDTO) {
        try{
            ComprobantesTiposVentasEntity comprobantesTiposVentasEntity = iComprobantesTiposVentasRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("No se encontro el tipo comprobante de venta con id: " + id));
            comprobantesTiposVentasEntity.setCodigo(comprobantesTiposVentasDTO.getCodigo());
            comprobantesTiposVentasEntity.setDescripcion(comprobantesTiposVentasDTO.getDescripcion());
            comprobantesTiposVentasEntity.setIdEmpresa(comprobantesTiposVentasDTO.getIdEmpresa());
            comprobantesTiposVentasEntity.setUsuarioActualizacion(comprobantesTiposVentasDTO.getUsuarioActualizacion());
            iComprobantesTiposVentasRepository.save(comprobantesTiposVentasEntity);
            return modelMapper.map(comprobantesTiposVentasEntity, ComprobantesTiposVentasDTO.class);
        } catch (Exception e) {
            log.error("Error al actualizar el tipo comprobante de venta: " + e.getMessage());
            throw new RuntimeException("Error al actualizar el tipo comprobante de venta: " + e.getMessage());
        }
    }

    @Override
    public void deleteById(Long id) {
        try{
            ComprobantesTiposVentasEntity comprobantesTiposVentasEntity = iComprobantesTiposVentasRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("No se encontro el estado de venta con id: " + id));
            iComprobantesTiposVentasRepository.delete(comprobantesTiposVentasEntity);
        } catch (Exception e) {
            log.error("Error al eliminar el tipo comprobante de venta: " + e.getMessage());
            throw new RuntimeException("Error al eliminar el tipo comprobante de venta: " + e.getMessage());
        }
    }

    @Override
    public Page<ComprobantesTiposVentasDTO> findAllByEmpresa(String codigo, String descripcion, Pageable pageable, Long idEmpresa) {
        try{
            Specification<ComprobantesTiposVentasEntity> specification = ComprobantesVentasTiposEspecification.getComprobantesVentasTipos(codigo, descripcion, idEmpresa);
            return iComprobantesTiposVentasRepository.findAll(specification, pageable).map(comprobantesTiposVentasEntity -> modelMapper.map(comprobantesTiposVentasEntity, ComprobantesTiposVentasDTO.class));
        } catch (Exception e) {
            log.error("Error al obtener los estados de venta: " + e.getMessage());
            throw new RuntimeException("Error al obtener los estados de venta: " + e.getMessage());
        }
    }
}
