package com.microservice.inventario.controller.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComprobanteDetalleRequest {
    private Long numero;
    @NotBlank(message = "La descripcion es obligatoria")
    private String descripcion;
    @NotNull(message = "La cantidad es obligatoria")
    private Integer cantidad;
    @NotNull(message = "El idProducto es obligatorio")
    private Long idProducto;
    @NotNull(message = "El idEnvase es obligatorio")
    private Long idEnvase;
    @NotNull(message = "El peso es obligatorio")
    private BigDecimal peso;
    @NotNull(message = "El precio unitario es obligatorio")
    private BigDecimal precioUnitario;
    @NotNull(message = "El descuento es obligatorio")
    private BigDecimal descuento;
    private BigDecimal tara;
}
