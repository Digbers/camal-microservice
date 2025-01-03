package com.microservice.ventas.controller.DTO.ventas;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComprobantesTiposVentasDTO {
    private Long id;
    private String codigo;
    private String descripcion;
    private Long idEmpresa;
    private String usuarioCreacion;
    private Timestamp fechaCreacion;
    private String usuarioActualizacion;
    private Timestamp fechaActualizacion;
}
