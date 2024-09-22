package com.camal.microservice_finanzas.controller.DTO;

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
