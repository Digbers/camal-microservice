package com.camal.microservice_auth.controller.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;

@Builder
@JsonPropertyOrder({"usercodigo","username", "idEmpresa", "idAlmacen", "idPuntoVenta", "message", "jwt", "status"})
public record AuthResponseComplete(
        String usercodigo,
        String username,
        Integer idEmpresa,
        Integer idAlmacen,
        Integer idPuntoVenta,
        String message,
        String jwt,
        boolean status
) {
}
