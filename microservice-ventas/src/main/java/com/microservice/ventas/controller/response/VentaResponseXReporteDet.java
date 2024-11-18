package com.microservice.ventas.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VentaResponseXReporteDet {
    private Long numero;
    private String descripcion;
    private BigDecimal peso;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal total;
    private BigDecimal descuento;
}
