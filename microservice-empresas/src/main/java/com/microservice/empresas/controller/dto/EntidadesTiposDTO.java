package com.microservice.empresas.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntidadesTiposDTO {
    private Long id;
    private Long empresa;
    private String tipoCodigo;
    private String descripcion;
    private String usuarioCreacion;
    private Timestamp fechaCreacion;
    private String usuarioActualizacion;
    private Timestamp fechaActualizacion;
}
