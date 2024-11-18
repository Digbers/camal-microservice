package com.microservice.ventas.controller.DTO.ventas;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComprobantesVentasCabDTO {
    private Long id;
    @NotNull(message = "El ID de la empresa es nulo")
    private Long idEmpresa;
    private ComprobantesTiposVentasDTO comprobantesTipos;
    private Long facturacionElectronicaEntity;
    @NotBlank(message = "La serie es obligatoria")
    private String serie;
    @NotBlank(message = "El numero es obligatorio")
    private String numero;
    @NotNull(message = "El ID del cliente es nulo")
    private Long idCliente;
    @NotNull(message = "El ID de la punto de venta es obligatorio")
    private Long idPuntoVenta;
    private String nombreCliente;
    private String numeroDocumentoCliente;
    @NotNull(message = "La fecha de emision es obligatoria")
    private LocalDate fechaEmision;
    @NotNull(message = "La fecha de vencimiento es obligatoria")
    private LocalDate fechaVencimiento;
    @NotNull(message = "El estado es obligatorio")
    private ComprobantesVentasEstadoDTO comprobantesVentaEstado;
    private String estadoCreacion;
    @Valid
    @NotNull(message = "La lista de detalles no puede ser nula")
    private List<ComprobantesVentasDetDTO> comprobantesVentaDet;
    @Valid
    private List<ComprobantesVentasCuotasDTO> comprobantesVentasCuotas;
    private String observacion;
    @NotBlank(message = "La moneda es obligatoria")
    private String codigoMoneda;
    private Double tipoCambio;
    @NotNull(message = "El usuario de creacion es obligatorio")
    private String usuarioCreacion;
    private String usuarioActualizacion;
}
