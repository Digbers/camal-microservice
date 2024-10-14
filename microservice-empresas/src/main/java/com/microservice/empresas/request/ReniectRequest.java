package com.microservice.empresas.request;

import jakarta.validation.constraints.NotBlank;

public record ReniectRequest(
        @NotBlank String dni
) {
}
