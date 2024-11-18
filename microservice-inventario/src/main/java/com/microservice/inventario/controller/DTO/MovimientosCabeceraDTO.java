package com.microservice.inventario.controller.DTO;

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
    private LocalDate fechaEmision;
    private BigDecimal total;
    private MovimientosMotivosDTO motivoCodigo;
    private String idUsuario;
    private String monedaCodigo;
    private List<MovimientosDetalleDTO> movimientosDetalles;
    private AlmacenDTO idAlmacen;
    private String tipoDocumentoReferencia;
    private String serieDocumentoReferencia;
    private String numeroDocumentoReferencia;
    private String observaciones;
    private Long idEntidad;
    private Integer cantidadEnvaces;
    private LocalDate fechaIngresoSalida;
    private String usuarioCreacion;
    private String usuarioActualizacion;
}
