package com.microservice.inventario.controller.DTO;

import com.microservice.inventario.persistence.entity.ProductosTiposEntity;
import com.microservice.inventario.persistence.entity.StockAlmacen;
import com.microservice.inventario.persistence.entity.UnidadesEntity;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDTO {
    private Long idProducto;
    @NotNull(message = "La empresa es obligatorio")
    private Long empresa;
    @NotBlank(message = "El código del producto no puede estar vacío")
    @Size(max = 50, message = "El código no puede tener más de 50 caracteres")
    private String codigo;
    @NotBlank(message = "El nombre del producto no puede estar vacío")
    private String nombre;
    @NotNull(message = "El tipo de producto es obligatorio")
    private String tipo;
    @NotNull(message = "La unidad es obligatoria")
    private String unidad;
    // Campos que se agregarían desde StockAlmacen
    private Long stockAlmacenId; // ID del StockAlmacen
    private Long almacenId;       // ID del Almacén
    private Long envaseId;        // ID del Envase
    private Long empresaId;       // ID de la Empresa
    private int cantidadEnvase; // Cantidad en envase
    private int cantidadProducto; // Cantidad total del producto
    private BigDecimal pesoTotal; // Peso total
    private LocalDate fechaRegistro; // Fecha de registro
    @NotNull(message = "Debe especificar si genera stock")
    private Boolean generarStock;
    @NotNull(message = "El estado es obligatorio")
    private Boolean estado;
    @NotNull(message = "El precio sugerido es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El precio de venta debe ser mayor que 0")
    private BigDecimal precioVenta;
    @DecimalMin(value = "0.0", inclusive = true, message = "El precio de compra debe ser mayor que 0")
    private BigDecimal precioCompra;
    @NotBlank(message = "El codigo del usuario creador es obligatorio")
    private String usuarioCreacion;
    private String usuarioActualizacion;
}