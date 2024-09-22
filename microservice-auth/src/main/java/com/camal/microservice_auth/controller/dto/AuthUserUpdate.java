package com.camal.microservice_auth.controller.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public record  AuthUserUpdate(
        @NotBlank String usercodigo,
        @NotBlank String username,
        @NotBlank String password,
        @Valid Boolean isEnabled,
        @Valid AuthCreateRoleRequest rolesRequest
) {
}
