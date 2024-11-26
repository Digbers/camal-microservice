package com.microservice.empresas.controller.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDate;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsistenciasDTO {
    private Long id;
    private Long  idEntidad;
    private LocalDate fechaAsistencia;
    private Boolean asistio;
    private String usuarioCreacion;
    private Timestamp fechaCreacion;
    private String usuarioActualizacion;
    private Timestamp fechaActualizacion;
}
