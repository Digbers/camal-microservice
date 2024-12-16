package com.microservice.inventario.controller.DTO.compras;

import com.microservice.inventario.controller.DTO.ComprobanteCabeceraDTO;
import com.microservice.inventario.controller.DTO.ComprobanteDetalleRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComprobantesComprasCaDTO implements ComprobanteCabeceraDTO<ComprobantesComprasCuotasDTO> {

    private Long id;
    @NotNull(message = "El id empresa es obligatorio")
    private Long idEmpresa;
    @NotBlank(message = "El serie es obligatorio")
    private String serie;
    @NotBlank(message = "El numero es obligatorio")
    private String numero;
    @NotNull(message = "La fecha de emision es obligatorio")
    private LocalDate fechaEmision;
    @NotNull(message = "La fecha de vencimiento es obligatorio")
    private LocalDate fechaVencimiento;
    @NotNull(message = "La fecha de ingreso es obligatorio")
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
    private List<ComprobantesComprasCuotasDTO> comprobantesComprasCuotaDTO;
    @NotNull(message = "El id proveedor es obligatorio")
    private Long idProveedor;
    @NotNull(message = "El usuario de creacion es obligatorio")
    private String usuarioCreacion;
    private String usuarioActualizacion;
    @Override
    public List<ComprobantesComprasCuotasDTO> getCuotas() {
        return comprobantesComprasCuotaDTO;
    }
    @Override
    public Object getComprobanteEstado() {
        return comprobanteCompraEstados;  // Retorna el estado específico para compras
    }
    @Override
    public Object getComprobantesTipos() {
        return comprobantesTipos;
    }

    @Override
    public List<ComprobanteDetalleRequest> obtenerDetalle() {
        return comprobantesComprasDetalle.stream()
                .map(det -> new ComprobanteDetalleRequest(
                        det.getNumero(),
                        det.getDescripcion(),
                        det.getCantidad(),
                        det.getIdProducto(),
                        det.getIdEnvase(),
                        det.getPeso(),
                        det.getPrecioUnitario(),
                        det.getDescuento(),
                        det.getTara()))
                .collect(Collectors.toList());
    }

    @Override
    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }
    @Override
    public LocalDate getPeriodoRegistro() {
        return periodoRegistro;
    }

    @Override
    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }
    @Override
    public Long getIdTercero() {
        return idProveedor;
    }
}
