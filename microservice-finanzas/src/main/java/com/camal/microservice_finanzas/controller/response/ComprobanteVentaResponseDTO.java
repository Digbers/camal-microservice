package com.camal.microservice_finanzas.controller.response;

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
public class ComprobanteVentaResponseDTO {
    private Long id;
    private String tipoComprobante;
    private String serie;
    private String numero;
    private String numeroDocumentoCliente;
    private String nombreCliente;
    private String codigoMoneda;
    private BigDecimal total; // Suma de precioUnitario de los detalles
    private LocalDate fechaEmision;
}