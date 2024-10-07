package com.camal.microservice_finanzas.event;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompraFailedEvent {
    @NotNull(message = "El ID de la compra es obligatorio")
    private Long id;
    @NotNull(message = "El spurce es obligatorio")
    private String source;
}
