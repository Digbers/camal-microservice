package com.microservice.inventario.controller.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnvaseDTO {
    private Long idEnvase;
    private Long idEmpresa;
    private String tipoEnvase;
    private String descripcion;
    private Integer capacidad; // Cantidad de pollos por envase
    private BigDecimal pesoReferencia;
    private Boolean estado; // Ej: "Vacío", "Lleno", "En Uso"
    private List<StockAlmacenDTO> stockAlmacenList;
    private String usuarioCreacion;
    private String usuarioActualizacion;
}
