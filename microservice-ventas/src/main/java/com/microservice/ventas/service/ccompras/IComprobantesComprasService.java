package com.microservice.ventas.service.ccompras;

import com.microservice.ventas.controller.DTO.compras.ComprobantesComprasCaDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface IComprobantesComprasService {
    Page<ComprobantesComprasCaDTO> findAll(LocalDate fechaEmision, LocalDate fechaCreacion,
                                           String codigoTipo, String serie, String numero,
                                           String numeroDoc, String nombre, BigDecimal subtotal, BigDecimal impuesto,
                                           BigDecimal total, String monedaCodigo, Double tipoCambio, String estadoCodigo,
                                           Long idPuntoVenta, Pageable pageable);
}
