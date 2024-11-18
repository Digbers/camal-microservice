package com.microservice.inventario.controller.DTO;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductosTiposDTO {
    private Long idTipoProducto;
    private Long idEmpresa;
    private String codigo;
    private String nombre;
    private String  usuCreacion;
    private String usuarioActualizacion;
}
