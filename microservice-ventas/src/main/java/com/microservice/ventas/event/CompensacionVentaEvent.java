package com.microservice.ventas.event;

import com.microservice.ventas.controller.DTO.ComprobanteDetalleRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompensacionVentaEvent {
    @NotNull(message = "El id de la venta es obligatorio")
    private Long ventaId;
    @NotNull(message = "Los detalles de la venta son obligatorios")
    @Valid
    private List<ComprobanteDetalleRequest> comprobanteDetalleRequest;
    private String serie;
    private String numero;
    private String usuarioCreacion;
    private String codigoMoneda;
    private String observacion;
    private Long idCliente;
    private LocalDate fechaEmision;
    @NotNull(message = "El id del punto de venta es obligatorio")
    private Long idPuntoVenta;
    @NotNull(message = "El ID de la empresa es obligatorio")
    private Long idEmpresa;
    @NotNull(message = "El source es obligatorio")
    private String source;
    private String codigoProductoVenta;
}
