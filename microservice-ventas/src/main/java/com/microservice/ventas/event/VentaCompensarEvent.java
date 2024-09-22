package com.microservice.ventas.event;

import com.microservice.ventas.controller.DTO.ComprobanteDetalleRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VentaCompensarEvent {
    private Long ventaId;
    private List<ComprobanteDetalleRequest> comprobanteDetalleRequest;
}
