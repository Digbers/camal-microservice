package com.microservice.inventario.controller.DTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.sql.Timestamp;
import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDTO {
    private Long id;
    @NotNull(message = "El ID de la empresa es obligatorio")
    private Long empresaId;

    @NotBlank(message = "El código del producto no puede estar vacío")
    @Size(max = 50, message = "El código no puede tener más de 50 caracteres")
    private String codigo;

    @NotBlank(message = "El nombre del producto no puede estar vacío")
    private String nombre;

    @NotNull(message = "El tipo de producto es obligatorio")
    private ProductosTiposDTO tipo;

    @NotNull(message = "La lista de productos por almacén no puede ser nula")
    @Valid
    private List<ProductosXAlmacenDTO> productosXAlmacen;

    @NotBlank(message = "La marca no puede estar vacía")
    private String marca;

    @NotBlank(message = "La presentación no puede estar vacía")
    private String presentacion;

    @NotNull(message = "La capacidad es obligatoria")
    @Min(value = 1, message = "La capacidad debe ser mayor a 0")
    private Integer capacidadCantidad;

    @NotNull(message = "Debe especificar si genera stock")
    private Boolean generarStock;

    @NotNull(message = "El estado es obligatorio")
    private Boolean estado;

    @NotNull(message = "El precio sugerido es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor que 0")
    private Double precioSugerido;

    @NotBlank(message = "El codigo del usuario creador es obligatorio")
    private String usuCreacion;
    private String usuActualizacion;
}