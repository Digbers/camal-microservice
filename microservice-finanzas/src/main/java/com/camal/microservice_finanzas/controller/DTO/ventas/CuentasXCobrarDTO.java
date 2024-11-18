package com.camal.microservice_finanzas.controller.DTO.ventas;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CuentasXCobrarDTO {
    private Long idComprobanteVenta;
    private String comprobanteTipo;
    private String serie;
    private String numero;
    private String numeroDoc;
    private String nombre;
    private String monedaCodigo;
    private BigDecimal total;
    private BigDecimal pagado;
    private BigDecimal saldo;
}
