package com.camal.microservice_finanzas.controller.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComprobantesComprasPagosDTO {
    private Long id;
    private Long idEmpresa;
    private Long idComprobanteCompra;
    private FormasPagosDTO formaPagos;
    private BigDecimal montoCobrado;
    private LocalDate fechaCobro;
    private String descripcion;
    private MonedasDTO moneda;
    private String usuarioCreacion;
    private String usuarioActualizacion;
}
