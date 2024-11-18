package com.microservice.ventas.controller.DTO;

import java.time.LocalDate;
import java.util.List;

public interface ComprobanteCabeceraDTO {
    Long getId();
    Long getIdEmpresa();
    String getSerie();
    String getNumero();
    LocalDate getFechaEmision();
    String getCodigoMoneda();
    String getUsuarioCreacion();
    List<ComprobanteDetalleRequest> obtenerDetalle();
}

