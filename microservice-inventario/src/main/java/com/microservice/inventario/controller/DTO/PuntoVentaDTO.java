package com.microservice.inventario.controller.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PuntoVentaDTO {
    private Long id;
    private Long idEmpresa;
    private String direccion;
    private String nombre;
    private UbigeoDTO ubigeo;
    private AlmacenDTO almacen;
    private String usuarioCreacion;
    private Timestamp fechaCreacion;
    private String usuarioActualizacion;
    private Timestamp fechaActualizacion;
}
