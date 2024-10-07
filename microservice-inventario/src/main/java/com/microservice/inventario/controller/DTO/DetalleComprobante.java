package com.microservice.inventario.controller.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.N;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DetalleComprobante {
    @NotNull(message = "El precio unitario no puede ser nulo")
    private BigDecimal precioUnitario;
    @NotNull(message = "La cantidad no puede ser nula")
    private Integer cantidad;
}
