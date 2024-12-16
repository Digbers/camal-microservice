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
public class ComprobantesVentasCobrosDTO {
    private Long id;
    private Long idEmpresa;
    private Long idComprobanteVenta;
    private String formasDeCobros;
    private BigDecimal montoCobrado;
    private LocalDate fechaCobro;
    private String descripcion;
    private String monedas;
    private String usuarioCreacion;
    private Timestamp fechaCreacion;
    private String usuarioActualizacion;
    private Timestamp fechaActualizacion;
}
