package com.microservice.ventas.controller.DTO.ventas;

import com.microservice.ventas.controller.DTO.CuotasRequestDTO;
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
    private List<CuotasRequestDTO> cuotasRequest;
    @NotNull(message = "El id de la almacen es obligatorio")
    private Long idAlmacen;
    @NotNull(message = "El ID de la forma de pago es obligatorio")
    private String codigoFormaCobro;
}
