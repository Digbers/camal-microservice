package com.camal.microservice_auth.controller.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;

import java.util.List;
@Validated
public record AuthCreateRoleRequest(
        @Size(max = 3, message = "El usuario no puede tener mas de 3 roles") List<String> roleListName) {
}
