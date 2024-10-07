package com.microservice.inventario.controller.DTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovimientosMotivosDTO {
    private String codigo;
    private String descripcion;
    private String usuarioCreacion;
    private String usuarioActualizacion;
}
