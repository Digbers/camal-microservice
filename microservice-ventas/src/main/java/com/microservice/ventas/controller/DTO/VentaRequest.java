package com.microservice.ventas.controller.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VentaRequest {
    @NotNull(message = "La cabecera del documento es obligatoria")
    private ComprobantesVentasCabDTO comprobantesVentasCabDTO;
    @NotNull(message = "La lista de forma de cobro es obligatoria")
    private FormasDeCobrosRequest formasDeCobrosRequest;
    @NotNull(message = "El id de la almacen es obligatorio")
    private Long idAlmacen;
}
