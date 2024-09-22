package com.microservice.inventario.controller.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductosXAlmacenDTO {
    private Long id;
    private Long empresaId;
    Long almacen;
    Long productoId;
    private Integer cantidad;
    private String usuCreacion;
}
