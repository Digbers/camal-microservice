package com.microservice.ventas.service.ventas;

import com.microservice.ventas.controller.DTO.ventas.ComprobantesVentasEstadoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IVentasEstadosService {
    List<ComprobantesVentasEstadoDTO> findByIdEmpresa(Long idEmpresa);
    Page<ComprobantesVentasEstadoDTO> findAllByEmpresa(String codigo, String descripcion, Pageable pageable, Long idEmpresa);
}
