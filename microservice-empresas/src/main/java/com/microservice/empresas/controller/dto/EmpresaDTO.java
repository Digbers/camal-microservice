package com.microservice.empresas.controller.dto;

import lombok.*;

import java.sql.Timestamp;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpresaDTO {
    private Long id;
    private String razonSocial;
    private String empresaCodigo;
    private String ruc;
    private String direccion;
    private String departamento;
    private String provincia;
    private String distrito;
    private String ubigeo;
    private String telefono;
    private String celular;
    private String correo;
    private String web;
    private String logo;
    private Boolean estado;
    private String usuarioCreacion;
    private String usuarioActualizacion;
}
