package com.camal.microservice_finanzas.controller.DTO;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonedasDTO {
    private Long id;
    @NotBlank(message = "El campo 'Codigo' no puede estar vacío")
    private String codigo;
    @NotNull(message = "El campo 'Id Empresa' no puede estar vacío")
    private Long idEmpresa;
    @NotBlank(message = "El campo 'Nombre' no puede estar vacío")
    private String nombre;
    @NotBlank(message = "El campo 'Simbolo' no puede estar vacío")
    private String simbolo;
    private String usuarioCreacion;
    private Timestamp fechaCreacion;
    private String usuarioActualizacion;
    private Timestamp fechaActualizacion;
}
