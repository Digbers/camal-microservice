package com.microservice.inventario.event;

import com.microservice.inventario.controller.DTO.compras.ComprobantesComprasCaDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class InventarioActualizadoCompraEvent {
    @NotNull(message = "La cabecera del documento es obligatoria")
    @Valid
    private ComprobantesComprasCaDTO comprobantesComprasCa;
    @NotNull(message = "El id de la almacen es obligatorio")
    private Long idAlmacen;
    @NotNull(message = "El ID de la forma de pago es obligatorio")
    @Length(min = 1, max = 3, message = "El ID de la forma de pago debe tener entre 1 y 3 caracteres")
    private String codigoFormaPago;
    @NotNull(message = "El monto total es obligatorio")
    private BigDecimal montoTotal;
    @Valid
    private List<ComprobantesComprasPagosEventDTO> comprobantesComprasPagosDTO;
}
