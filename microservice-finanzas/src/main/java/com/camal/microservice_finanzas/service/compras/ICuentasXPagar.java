package com.camal.microservice_finanzas.service.compras;


import com.camal.microservice_finanzas.controller.DTO.compras.ComprobantesComprasPagosDTO;
import com.camal.microservice_finanzas.controller.DTO.compras.CuentasXPagarDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ICuentasXPagar {
    Page<CuentasXPagarDTO> findAll(Boolean inPagado, LocalDate fechaEmision, String comprobanteTipo, String serie, String numero, String numeroDoc, String nombre, String monedaCodigo, BigDecimal total, BigDecimal pagado, BigDecimal saldo, Pageable pageable);
    List<ComprobantesComprasPagosDTO> findPagosByComprobante(Long idComprobante);
    ComprobantesComprasPagosDTO savePago(ComprobantesComprasPagosDTO comprobantesVentasCobrosDTO);
    ComprobantesComprasPagosDTO updatePago(Long id, ComprobantesComprasPagosDTO comprobantesComprasPagosDTO);
}
