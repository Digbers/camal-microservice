package com.camal.microservice_auth.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AuthLoginComplete(@NotBlank String usercodigo,
                                @NotBlank String username,
                                @NotNull Integer idEmpresa,
                                @NotNull Integer idAlmacen,
                                @NotNull Integer idPuntoVenta
) {
}
