package com.microservice.empresas.controller.dto;

import com.microservice.empresas.persistence.entity.EmpresaEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.sql.Timestamp;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentosTiposDTO {
    private EmpresaEntity empresa;
    private String docCodigo;
    private String descripcion;
    private String usuarioCreacion;
    private Timestamp fechaCreacion;
    private String usuarioActualizacion;
    private Timestamp fechaActualizacion;
}
