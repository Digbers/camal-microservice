package com.microservice.empresas.response;

import com.microservice.empresas.controller.dto.AsistenciasDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntidadResponseAsistencias {
    private Long id;
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private List<AsistenciaResponseDTO> asistencias;
}
