package com.microservice.inventario.controller.DTO.ventas;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComprobantesVentasDetDTO {
    private Long id;
    //@NotNull(message = "El ID del comprobante es nulo")
    private Long comprobanteCabecera;
    @NotBlank(message = "El numero del detalle es obligatorio")
    private Long numero;
    @NotBlank(message = "La descripcion es obligatoria")
    private String descripcion;
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    private Integer cantidad;
    @NotNull(message = "El ID del producto es nulo")
    private Long idProducto;
    @NotNull(message = "El ID del envase es obligatorio")
    private Long idEnvase;
    @NotNull(message = "El peso es obligatorio")
    @DecimalMin(value ="0.0", inclusive = false, message = "El peso debe ser mayor que 0")
    private BigDecimal peso;
    @NotNull(message = "El precio unitario es obligatorio")
    @DecimalMin(value ="0.0", inclusive = false, message = "El precio debe ser mayor que 0")
    private BigDecimal precioUnitario;
    @NotNull(message = "El descuento es obligatorio")
    @DecimalMin(value ="0.0", message = "El descuento debe ser mayor o igual a 0 0")
    private BigDecimal descuento;
    @NotBlank(message = "El ID del usuario creador es obligatorio")
    private String usuarioCreacion;
}
