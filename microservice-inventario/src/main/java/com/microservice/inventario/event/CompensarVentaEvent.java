package com.microservice.inventario.event;


import com.microservice.inventario.controller.DTO.ComprobanteDetalleRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompensarVentaEvent {
    @NotNull(message = "El id de la venta es obligatorio")
    private Long ventaId;
    @NotNull(message = "Los detalles de la venta son obligatorios")
    @Valid
    private List<ComprobanteDetalleRequest> comprobanteDetalleRequest;
    @NotNull(message = "El id del punto de venta es obligatorio")
    private Long idPuntoVenta;
    @NotNull(message = "El ID de la empresa es obligatorio")
    private Long idEmpresa;
    @NotNull(message = "El source es obligatorio")
    private String source;
}
