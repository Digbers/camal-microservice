package com.microservice.inventario.controller.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoLightDTO {
    private Long id;
    private String codigo;
    private String nombre;
    private String marca;
}
