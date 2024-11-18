package com.microservice.empresas.request;

import jakarta.validation.constraints.NotBlank;

public record EntidadRequest(
        @NotBlank String numero,
        String nombre_completo,
        String nombres,
        String apellido_paterno,
        String apellido_materno,
        String direccion
) {
}
