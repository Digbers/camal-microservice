package com.microservice.ventas.controller.DTO;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormasCobrosDTO {
    @NotNull(message = "El ID de la forma de cobro es obligatorio")
    @Length(min = 1, max = 3, message = "El ID de la forma de cobro debe tener entre 1 y 3 caracteres")
    private String codigo;
    @NotNull(message = "El monto de la forma de cobro es obligatorio")
    @DecimalMin(value ="0.0", inclusive = false, message = "El monto de la forma de cobro debe ser mayor que 0")
    private BigDecimal monto;
}
