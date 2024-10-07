package com.microservice.inventario.controller.DTO.compras;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ComprobantesComprasTiposDTO {
    private String codigo;
    private String descripcion;
    private Long idEmpresa;
    private String usuarioCreacion;
    private String usuarioActualizacion;

}
