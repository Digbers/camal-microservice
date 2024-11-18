package com.microservice.inventario.controller.DTO;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnidadesDTO {
    private Long idUnidad;
    private Long idEmpresa;
    private String codigo;
    private String nombre;
    private String usuarioCreacion;
    private String usuarioActualizacion;
}
