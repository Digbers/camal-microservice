package com.microservice.empresas.response;

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
public class AsistenciaResponseDTO {
    private Long  idEntidad;
    private LocalDate fechaAsistencia;
    private Boolean asistio;
}
