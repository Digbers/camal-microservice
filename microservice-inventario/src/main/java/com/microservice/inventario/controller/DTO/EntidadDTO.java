package com.microservice.inventario.controller.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntidadDTO {
    private Long id;
    private String nombre;
    private String documentoTipo;
    private String nroDocumento;
}
