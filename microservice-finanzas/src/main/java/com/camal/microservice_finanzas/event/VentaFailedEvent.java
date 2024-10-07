package com.camal.microservice_finanzas.event;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VentaFailedEvent {
    @NotNull(message = "El ID de la venta es obligatorio")
    private Long id;
    @NotNull(message = "El spurce es obligatorio")
    private String source;
}
