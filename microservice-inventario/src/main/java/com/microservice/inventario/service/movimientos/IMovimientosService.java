package com.microservice.inventario.service.movimientos;

import com.microservice.inventario.controller.DTO.MovimientosCabeceraDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface IMovimientosService {
    Page<MovimientosCabeceraDTO> finAll(Long id, Long idEmpresa, Long numero, LocalDate fechaEmision, BigDecimal total, String motivoCodigo, String idUsuario, String monedaCodigo, Pageable pageable);
}
