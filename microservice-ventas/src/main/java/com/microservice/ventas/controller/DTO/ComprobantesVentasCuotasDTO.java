package com.microservice.ventas.controller.DTO;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComprobantesVentasCuotasDTO {
    private Long id;
    @NotNull(message = "El ID de la empresa es nulo")
    private Long idEmpresa;
    private Long idComprobanteCabecera;
    @NotNull(message = "El nro de cuota es obligatorio")
    private Integer nroCuota;
    @NotNull(message = "La fecha de vencimiento es obligatoria")
    private Date fechaVencimiento;
    @NotNull(message = "El importe es obligatorio")
    @DecimalMin(value ="0.0", inclusive = false, message = "El importe debe ser mayor 0")
    private BigDecimal importe;
    @NotNull(message = "El codigo de la moneda es obligatorio")
    private String codigoMoneda;
    @NotNull(message = "El ID del usuario es obligatorio")
    private String usuarioCreacion;
    private Timestamp fechaCreacion;
    private String usuarioActualizacion;
    private Timestamp fechaActualizacion;
}
