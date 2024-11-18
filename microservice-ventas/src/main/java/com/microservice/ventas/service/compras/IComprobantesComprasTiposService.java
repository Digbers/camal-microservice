package com.microservice.ventas.service.compras;

import com.microservice.ventas.controller.DTO.compras.ComprobantesComprasTiposDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IComprobantesComprasTiposService {
    Page<ComprobantesComprasTiposDTO> findAllByEmpresa(String codigo, String descripcion, Pageable pageable, Long idEmpresa);
}
