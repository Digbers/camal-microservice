package com.microservice.empresas.controller.dto;

import com.microservice.empresas.persistence.entity.EmpresaEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ZonasDTO {
    private Long empresa;
    private Long id;
    private String nombre;
    private String ubiCodigo;
    private String departamento;
    private String provincia;
    private String distrito;
    private String usuarioCreacion;
    private String usuarioActualizacion;

}
