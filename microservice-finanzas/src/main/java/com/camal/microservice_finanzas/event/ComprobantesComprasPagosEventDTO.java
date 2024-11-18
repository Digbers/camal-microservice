package com.camal.microservice_finanzas.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ComprobantesComprasPagosEventDTO {
    private Long id;
    private Long idEmpresa;
    private Long idComprobanteCompra;
    private Long formaPagos;      // Usar ID en lugar del objeto completo
    private BigDecimal montoCobrado;
    private LocalDate fechaCobro;
    private String descripcion;
    private Long moneda;          // Usar ID en lugar del objeto completo
    private String usuarioCreacion;
    private String usuarioActualizacion;
}
