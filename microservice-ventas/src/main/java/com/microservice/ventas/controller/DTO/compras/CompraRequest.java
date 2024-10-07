package com.microservice.ventas.controller.DTO.compras;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraRequest {
    @NotNull(message = "La cabecera del documento es obligatoria")
    private ComprobantesComprasCaDTO comprobantesComprasCa;
    @NotNull(message = "El ID de la forma de pago es obligatorio")
    @Length(min = 1, max = 3, message = "El ID de la forma de pago debe tener entre 1 y 3 caracteres")
    private String codigoFormaPago;
    @NotNull(message = "El id de la almacen es obligatorio")
    private Long idAlmacen;
}
