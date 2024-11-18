package com.microservice.inventario.controller.DTO;

import com.microservice.inventario.controller.DTO.ventas.ComprobantesVentasCuotasDTO;

import java.time.LocalDate;
import java.util.List;

public interface ComprobanteCabeceraDTO<T> {
    Long getId();
    Long getIdEmpresa();
    String getSerie();
    String getNumero();
    LocalDate getFechaEmision();
    String getCodigoMoneda();
    Double getTipoCambio();
    String getUsuarioCreacion();
    String getObservacion();
    List<ComprobanteDetalleRequest> obtenerDetalle();

    // Método opcional para lista de cuotas, específico de ventas
    List<T> getCuotas();

    default Object getComprobanteEstado() {
        return null;  // Predeterminado a null si no es implementado
    }

    default Object getComprobantesTipos() {
        return null;  // Predeterminado a null si no es implementado
    }

    // Método opcional para el ID de tercero (cliente o proveedor)
    default Long getIdTercero() {
        return null;
    }

    // Métodos adicionales específicos de compras
    default LocalDate getFechaVencimiento() {
        return null;  // Predeterminado a null si no es implementado
    }

    default LocalDate getFechaIngreso() {
        return null;
    }

    default LocalDate getPeriodoRegistro() {
        return null;
    }
}
