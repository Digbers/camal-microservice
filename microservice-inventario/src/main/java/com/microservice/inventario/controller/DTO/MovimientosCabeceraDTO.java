package com.microservice.inventario.controller.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.microservice.inventario.persistence.entity.AlmacenEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovimientosCabeceraDTO {
    private Long id;
    private Long idEmpresa;
    private Long numero;
    private String serie;
    private LocalDate fechaEmision;
    private BigDecimal total;
    private String motivoCodigo;// es el estadopar aqui de la venta o compra
    private String estadoCodigo;
    private String idUsuario;
    private String monedaCodigo;
    private Long idAlmacen;
    private String documentoReferencia;
    private String observaciones;
    private Long idEntidad;
    private Integer cantidadEnvaces;
    private LocalDate fechaIngresoSalida;
    private String usuarioCreacion;
    private String usuarioActualizacion;
}
