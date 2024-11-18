package com.microservice.inventario.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComprobantesVentasCobrosEventDTO {
    private Long id;
    private Long idEmpresa;
    private Long idComprobanteVenta;
    private String formasDeCobros;
    private BigDecimal montoCobrado;
    private LocalDate fechaCobro;
    private String descripcion;
    private String moneda;
    private String usuarioCreacion;
}
