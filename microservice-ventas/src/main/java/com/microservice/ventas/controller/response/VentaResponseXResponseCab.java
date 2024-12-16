package com.microservice.ventas.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VentaResponseXResponseCab {
    private String empresa;
    private String rucEmpresa;
    private String empresaDireccion;
    private String empresaTelefono;
    private String empresaCelular;
    private String serieNumero;
    private String numeroDocumento;
    private LocalDate fechaEmision;
    private LocalDate fechaVencimiento;
    private String nombreCompleto;
    private String direccion;
    private String estadoComprobante;
    private String observacion;
    private String usuCodigo;
    private BigDecimal igv;
    private BigDecimal descuentoTotal;
    private BigDecimal importeTotal;
    private String totalTexto;//
    private String moneda;
    private String puntoVenta;
    private String puntoVentaDireccion;
    private String empresaCorreo;
    private BigDecimal importeTotalNeto;
    private String monedaCodigo;
    private String qr;

}
