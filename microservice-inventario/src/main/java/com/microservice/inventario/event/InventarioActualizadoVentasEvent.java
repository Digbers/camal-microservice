package com.microservice.inventario.event;

import com.microservice.inventario.controller.DTO.compras.ComprobantesComprasCaDTO;
import com.microservice.inventario.controller.DTO.ventas.ComprobantesVentasCabDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

@Data
@Builder
public class InventarioActualizadoVentasEvent {
    @NotNull(message = "La cabecera del documento es obligatoria")
    @Valid
    private ComprobantesVentasCabDTO comprobantesVentasCab;
    @NotNull(message = "El id de la almacen es obligatorio")
    private Long idAlmacen;
    @NotNull(message = "El ID de la forma de pago es obligatorio")
    @Length(min = 1, max = 3, message = "El ID de la forma de pago debe tener entre 1 y 3 caracteres")
    private String codigoFormaPago;
    @NotNull(message = "El monto total es obligatorio")
    private BigDecimal montoTotal;
}
