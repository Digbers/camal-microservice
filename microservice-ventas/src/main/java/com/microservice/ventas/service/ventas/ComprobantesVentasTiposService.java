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
