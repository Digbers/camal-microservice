package com.microservice.ventas.controller.DTO;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComprobanteDetalleRequest {
    @NotNull(message = "La cantidad es obligatoria")
    private Integer cantidad;
    @NotNull(message = "El idProducto es obligatorio")
    private Long idProducto;
}
