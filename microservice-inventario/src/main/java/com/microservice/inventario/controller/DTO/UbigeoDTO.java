package com.microservice.inventario.controller.DTO;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UbigeoDTO {
    private Long id;
    private String ubigeoCodigo;
    private String nombre;
    private String departamento;
    private String provincia;
    private String distrito;
    private String departamentoCompleto;
    private String provinciaCompleto;
    private String distritoCompleto;
    private String usuarioCreacion;
    private String usuarioActualizacion;
}
