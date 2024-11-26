package com.microservice.ventas.controller.DTO.compras;

import com.microservice.ventas.entity.ComprobantesComprasCuotasEntity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComprobantesComprasCaDTO {

    private Long id;
    @NotNull(message = "El id empresa es obligatorio")
    private Long idEmpresa;
    @NotBlank(message = "El serie es obligatorio")
    private String serie;
    @NotBlank(message = "El numero es obligatorio")
    private String numero;
    @NotNull(message = "La fecha de emision es obligatorio")
    private LocalDate fechaEmision;
    @NotNull(message = "La fecha de vencimiento es obligatoria.")
    private LocalDate fechaVencimiento;
    @NotNull(message = "La fecha de ingreso es obligatoria")
    private LocalDate fechaIngreso;
    @NotNull(message = "El periodo de registro es obligatorio")
    private LocalDate periodoRegistro;
    private String observacion;
    @NotBlank(message = "El id moneda es obligatorio")
    private String codigoMoneda;
    @NotNull(message = "El tipo de cambio es obligatorio")
    private Double tipoCambio;
    private ComprobantesComprasTiposDTO comprobantesTipos;
    @Valid
    @NotNull(message = "Los estados de la compra no pueden ser nulos")
    private ComprobantesComprasEstadosDTO comprobanteCompraEstados;
    private String estadoCreacion;
    @NotNull(message = "El id punto de venta es obligatorio")
    private Long idPuntoVenta;
    @Valid
    @NotNull(message = "La lista de detalles no puede ser nula")
    private List<ComprobantesComprasDetalleDTO> comprobantesComprasDetalle;
    @Valid
    private List<ComprobantesComprasCuotasDTO> comprobantesComprasCuotas;
    @NotNull(message = "El id proveedor es obligatorio")
    private Long idProveedor;
    private String nombreProveedor;
    private String nroDocumentoProveedor;
    @NotNull(message = "El usuario de creacion es obligatorio")
    private String usuarioCreacion;
    private String usuarioActualizacion;
    private BigDecimal subtotal;
    private BigDecimal impuesto;
    private BigDecimal total;
}
