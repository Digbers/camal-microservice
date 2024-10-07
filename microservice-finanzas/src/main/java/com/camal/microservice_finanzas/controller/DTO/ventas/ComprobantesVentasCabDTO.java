package com.camal.microservice_finanzas.controller.DTO.ventas;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComprobantesVentasCabDTO {
    //@NotNull(message = "El ID del comprobante es nulo")
    private Long id;
    @NotNull(message = "El ID de la empresa es nulo")
    private Long idEmpresa;
    private ComprobantesTiposVentasDTO comprobantesTipos;
    @NotBlank(message = "La serie es obligatoria")
    private String serie;
    @NotBlank(message = "El numero es obligatorio")
    private String numero;
    @NotNull(message = "El ID del cliente es nulo")
    private Long idCliente;
    @NotNull(message = "El ID de la punto de venta es obligatorio")
    private Long idPuntoVenta;
    @NotNull(message = "La fecha de emision es obligatoria")
    private LocalDate fechaEmision;
    @NotNull(message = "El estado es obligatorio")
    private ComprobantesVentasEstadoDTO comprobantesVentaEstadoEntity;
    @Valid
    @NotNull(message = "La lista de detalles no puede ser nula")
    private List<ComprobantesVentasDetDTO> comprobantesVentaDet;
    @Valid
    private List<ComprobantesVentasCuotasDTO> comprobantesVentasCuotas;
    private String observacion;
    @NotBlank(message = "La moneda es obligatoria")
    private String codigoMoneda;
    @NotNull(message = "El usuario de creacion es obligatorio")
    private String usuarioCreacion;
    private Timestamp fechaCreacion;
    private String usuarioActualizacion;
    private Timestamp fechaActualizacion;
}
