package com.microservice.inventario.controller.DTO;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.microservice.inventario.persistence.entity.AlmacenTipoEntity;

import com.microservice.inventario.persistence.entity.TipoEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlmacenDTO {
    private Long id;
    private Long idEmpresa;
    private Long almacenPadre;
    private String nombre;
    private TipoEnum tipoAlmacen;
    private String usuarioCreacion;
    private Timestamp fechaCreacion;
    private String usuarioActualizacion;
    private Timestamp fechaActualizacion;

}
