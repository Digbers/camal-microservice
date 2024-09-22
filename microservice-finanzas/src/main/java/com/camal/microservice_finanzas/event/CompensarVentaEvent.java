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
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompensarVentaEvent {
    @NotNull(message = "El id de la venta es obligatorio")
    private Long ventaId;
    @NotNull(message = "Los detalles de la venta son obligatorios")
    @Valid
    private List<ComprobanteDetalleRequest> comprobanteDetalleRequest;
    @NotNull(message = "El id de la almacen es obligatorio")
    private Long idAlmacen;
}