package com.camal.microservice_finanzas.service.ventas;

import com.camal.microservice_finanzas.controller.DTO.ComprobantesVentasCobrosDTO;
import com.camal.microservice_finanzas.controller.DTO.ventas.CuentasXCobrarDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ICuentasXCobrarService {
    Page<CuentasXCobrarDTO> findAll(Boolean inCobrados, LocalDate fechaEmision, String comprobanteTipo, String serie, String numero, String numeroDoc, String nombre, String monedaCodigo, BigDecimal total, BigDecimal pagado, BigDecimal saldo, Pageable pageable);
    List<ComprobantesVentasCobrosDTO> findCobrosByComprobante(Long idComprobante);
    ComprobantesVentasCobrosDTO saveCobro(ComprobantesVentasCobrosDTO comprobantesVentasCobrosDTO);
    ComprobantesVentasCobrosDTO updateCobro( Long id, ComprobantesVentasCobrosDTO comprobantesVentasCobrosDTO);
}
