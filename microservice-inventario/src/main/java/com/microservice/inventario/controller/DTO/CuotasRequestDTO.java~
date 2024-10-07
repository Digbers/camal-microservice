package com.microservice.ventas.controller.DTO;

import jakarta.validation.constraints.NotNull;
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
public class CuotasRequestDTO {
    @NotNull(message = "El numero de cuota es obligatorio")
    private Integer numeroCuota;
    @NotNull(message = "El importe total es obligatorio")
    private BigDecimal importeTotal;
    @NotNull(message = "La fecha de vencimiento es obligatoria")
    private LocalDate fechaVencimiento;
}
