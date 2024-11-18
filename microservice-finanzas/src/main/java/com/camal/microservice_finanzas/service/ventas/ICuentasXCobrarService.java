package com.camal.microservice_finanzas.service.ventas;

import com.camal.microservice_finanzas.controller.DTO.ventas.CuentasXCobrarDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface ICuentasXCobrarService {
    Page<CuentasXCobrarDTO> findAll(String comprobanteTipo, String serie, String numero, String numeroDoc, String nombre, String monedaCodigo, BigDecimal total, BigDecimal pagado, BigDecimal saldo, Pageable pageable);
}
