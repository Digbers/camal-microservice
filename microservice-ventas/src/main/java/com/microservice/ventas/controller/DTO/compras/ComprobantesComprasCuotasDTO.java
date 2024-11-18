package com.microservice.ventas.controller.DTO.compras;

import com.microservice.ventas.entity.ComprobantesComprasCaEntity;
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
public class ComprobantesComprasCuotasDTO {
    private Long id;
    private Long idEmpresa;
    private Long idComprobante;
    private Integer nroCuota;
    private LocalDate fechaVencimiento;
    private BigDecimal importe;
    private String codigoMoneda;
    private String usuarioCreacion;
    private Timestamp fechaCreacion;
    private String usuarioActualizacion;
}
