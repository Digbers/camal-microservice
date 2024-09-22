package com.microservice.ventas.controller.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.microservice.ventas.entity.ComprobantesTiposVentasEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.sql.Timestamp;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeriesDTO {
    private Long id;
    private Long idEmpresa;
    private Long idPuntoVenta;
    @JsonIgnore
    private ComprobantesTiposVentasEntity codigoTipoComprobante;
    private String codigoSerie;
    private Boolean defaultSerie;
    private String usuarioCreacion;
    private Timestamp fechaCreacion;
    private String usuarioActualizacion;
    private Timestamp fechaActualizacion;
}
