package com.microservice.inventario.controller.DTO.response;

import jakarta.validation.constraints.NotBlank;

public record DatosGeneralesResponse(
        @NotBlank  String nombreEmpresa,
        @NotBlank String nombreAlmacen,
        @NotBlank String nombrePuntoVenta
) {
}
