package com.microservice.ventas.controller.DTO.ventas;

import com.microservice.ventas.event.ComprobantesVentasCobrosEventDTO;
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
public class VentaRequest {
    @NotNull(message = "La cabecera del documento es obligatoria")
    private ComprobantesVentasCabDTO comprobantesVentasCabDTO;
    @NotNull(message = "El id de la almacen es obligatorio")
    private Long idAlmacen;
    @NotNull(message = "El codigo de estado es obligatorio")
    private String codigoEstado; // CRE, CON
    @Valid
    private List<ComprobantesVentasCobrosEventDTO> comprobantesVentasCobrosDTO;
    private String codigoProductoVenta;
}
