package com.microservice.inventario.service.movimientos;

import com.microservice.inventario.controller.DTO.MovimientosCabeceraDTO;
import com.microservice.inventario.controller.DTO.MovimientosDetalleDTO;
import com.microservice.inventario.controller.DTO.MovimientosEstadosDTO;
import com.microservice.inventario.controller.DTO.MovimientosMotivosDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface IMovimientosService {
    Page<MovimientosCabeceraDTO> finAll(Long id, Long idEmpresa, Long numero, LocalDate fechaEmision, BigDecimal total, String motivoCodigo, String idUsuario, String monedaCodigo, Pageable pageable);
    List<MovimientosDetalleDTO> findByIdMovimiento(Long id);
    List<MovimientosMotivosDTO> findAllMotivos();
    List<MovimientosEstadosDTO> findAllEstados();
}
