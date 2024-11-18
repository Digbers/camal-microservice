package com.microservice.inventario.controller.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoAResponse {
    private Long id;
    private Long empresaId;
    private String codigo;
    private String descripcionA;
    private String nombre;
    private String unidad;
    private Integer cantidad;
    private BigDecimal precio;
    private Long envaseId;
    private BigDecimal peso;
    private Integer capacidadEnvase;
}
