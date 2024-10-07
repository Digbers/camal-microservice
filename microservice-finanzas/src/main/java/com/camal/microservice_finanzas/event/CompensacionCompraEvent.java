package com.camal.microservice_finanzas.event;

import com.camal.microservice_finanzas.controller.DTO.ComprobanteDetalleRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompensacionCompraEvent {
    @NotNull(message = "El ID de la compra es obligatorio")
    private Long id;
    @NotNull(message = "Los detalles de la compra son obligatorios")
    @Valid
    private List<ComprobanteDetalleRequest> comprobanteDetalleRequest;
    @NotNull(message = "El id de la almacen es obligatorio")
    private Long idAlmacen;
    @NotNull(message = "El spurce es obligatorio")
    private String source;
}
