package com.microservice.ventas.service.ventas;

import com.microservice.ventas.controller.DTO.ventas.ComprobantesTiposVentasDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface IComprobantesVentasTiposService {
    Page<ComprobantesTiposVentasDTO> findAllByEmpresa(String codigo, String descripcion, Pageable pageable, Long idEmpresa);
}
