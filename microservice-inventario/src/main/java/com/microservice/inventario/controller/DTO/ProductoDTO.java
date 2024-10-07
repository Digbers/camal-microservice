package com.microservice.inventario.controller.DTO;

import com.microservice.inventario.persistence.entity.ProductosTiposEntity;
import com.microservice.inventario.persistence.entity.StockAlmacen;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.math.BigDecimal;
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
    @OneToMany(mappedBy = "producto", cascade = {CascadeType.MERGE})
    private List<StockAlmacenDTO> stockAlmacenList;

    @NotNull(message = "Debe especificar si genera stock")
    private Boolean generarStock;

    @NotNull(message = "El estado es obligatorio")
    private Boolean estado;

    @NotNull(message = "El precio sugerido es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El precio debe ser mayor que 0")
    private BigDecimal precioSugerido;

    @NotBlank(message = "El codigo del usuario creador es obligatorio")
    private String usuCreacion;
    private String usuActualizacion;
}