package com.microservice.ventas.event;

import com.microservice.ventas.controller.DTO.ComprobanteDetalleRequest;
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
    @NotNull(message = "El id del punto de venta es obligatorio")
    private Long idPuntoVenta;
    @NotNull(message = "El id de la empresa es obligatorio")
    private Long idEmpresa;
    @NotNull(message = "El source es obligatorio")
    private String source;
}
