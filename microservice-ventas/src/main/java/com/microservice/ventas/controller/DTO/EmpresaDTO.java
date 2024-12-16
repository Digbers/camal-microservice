package com.microservice.ventas.controller.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpresaDTO {
    @NotNull(message = "El ID de la empresa es obligatorio")
    private Long id;
    @NotBlank(message = "El razón social de la empresa no puede estar vacío")
    private String razonSocial;
    @NotBlank(message = "El código de la empresa no puede estar vacío")
    private String empresaCodigo;
    private String ruc;
    private String direccion;
    private String telefono;
    private String celular;
    private String correo;
    private String web;
    private String logo;
}
