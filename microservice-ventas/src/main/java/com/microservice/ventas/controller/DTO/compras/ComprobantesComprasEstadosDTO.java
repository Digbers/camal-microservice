package com.microservice.ventas.controller.DTO.compras;


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
public class ComprobantesComprasEstadosDTO {
    @NotBlank(message = "El codigo es obligatorio")
    private String codigo;
    @NotNull(message = "El ID empresa es nulo")
    private Long idEmpresa;
    @NotBlank(message = "La descripcion es obligatoria")
    private String descripcion;
    @NotNull(message = "El ID del usuario creador es nulo")
    private String usuarioCreacion;
    private String usuarioActualizacion;

}
