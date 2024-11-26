package com.microservice.empresas.request;

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
public class AsistenciaRequest {
    private Long  idEntidad;
    private LocalDate fechaAsistencia;
    private Boolean asistio;
    private String usuarioCreacion;
    private String usuarioActualizacion;
}
