package com.microservice.empresas.controller.dto;

import com.microservice.empresas.persistence.entity.DocumentoTiposEntity;
import com.microservice.empresas.persistence.entity.EmpresaEntity;
import com.microservice.empresas.persistence.entity.EntidadesTiposEntity;
import com.microservice.empresas.persistence.entity.ZonasEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.sql.Timestamp;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntidadDTO {
    private EmpresaEntity empresa;
    private Long id;
    private ZonasEntity zona;
    private String nombre;

    private String apellidoPaterno;

    private String apellidoMaterno;

    private DocumentoTiposEntity documentoTipo;

    private String nroDocumento;
    private String email;
    private String celular;
    private String direccion;
    private String sexo;
    private Boolean estado;
    private String usuarioCreacion;

    private Timestamp fechaCreacion;

    private String usuarioActualizacion;

    private Timestamp fechaActualizacion;

    private List<EntidadesTiposEntity> entidadesTiposList;
}
