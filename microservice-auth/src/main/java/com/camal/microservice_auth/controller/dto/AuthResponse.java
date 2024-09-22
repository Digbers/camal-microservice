package com.camal.microservice_auth.controller.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;

@Builder
@JsonPropertyOrder({"usercodigo","username", "message", "jwt", "status"})
public record AuthResponse(String usercodigo,
                            String username,
                           String message,
                           String jwt,
                           boolean status) {
}
