package com.microservice.ventas.event;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraFailedEvent {
    @NotNull(message = "El ID de la compra es obligatorio")
    private Long id;
    @NotNull(message = "El spurce es obligatorio")
    private String source;
}
