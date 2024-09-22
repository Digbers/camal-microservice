package com.camal.microservice_finanzas.event;

import com.camal.microservice_finanzas.controller.DTO.ComprobanteDetalleRequest;
import com.camal.microservice_finanzas.controller.DTO.FormasDeCobrosRequest;
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
public class VentaCreadaEvent {
    @NotNull(message = "La cabecera del documento es obligatoria")
    private Long idComprobanteVenta;
    @NotNull(message = "La fecha de cobro es obligatoria")
    private FormasDeCobrosRequest formasDeCobrosRequest;
    @NotNull(message = "La fecha de cobro es obligatoria")
    private LocalDate fechaCobro;
    @NotNull(message = "La moneda es obligatoria")
    private String moneda;
    @NotNull(message = "El id del usuario es obligatorio")
    private String idUsuario;
    @NotNull(message = "El id de la empresa es obligatorio")
    private Long idEmpresa;
    @Valid
    @NotNull(message = "Los detalles de la venta son obligatorios")
    private List<ComprobanteDetalleRequest> comprobanteDetalleRequest;
    @NotNull(message = "El id de la almacen es obligatorio")
    private Long idAlmacen;

}