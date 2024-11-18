package com.microservice.inventario.controller.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AlmacenTipoDTO {
    private Long id;
    private String codigo;
    private String descripcion;
    private Long idEmpresa;
    private String usuarioCreacion;
    private String usuarioActualizacion;
}
