package com.microservice.inventario.controller.DTO.ventas;

import com.microservice.inventario.controller.DTO.ComprobanteCabeceraDTO;
import com.microservice.inventario.controller.DTO.ComprobanteDetalleRequest;
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
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComprobantesVentasCabDTO implements ComprobanteCabeceraDTO<ComprobantesVentasCuotasDTO> {
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
    private ComprobantesVentasEstadoDTO comprobantesVentaEstado;
    @Valid
    @NotNull(message = "La lista de detalles no puede ser nula")
    private List<ComprobantesVentasDetDTO> comprobantesVentaDet;
    @Valid
    private List<ComprobantesVentasCuotasDTO> comprobantesVentasCuotas;
    private String observacion;
    @NotBlank(message = "La moneda es obligatoria")
    private String codigoMoneda;
    @NotNull(message = "El tipo de cambio es obligatorio")
    private Double tipoCambio;
    @NotNull(message = "El usuario de creacion es obligatorio")
    private String usuarioCreacion;
    private String usuarioActualizacion;
    @Override
    public Object getComprobanteEstado() {
        return comprobantesVentaEstado;  // Retorna el estado específico para ventas
    }
    @Override
    public Object getComprobantesTipos() {
        return comprobantesTipos;  // Retorna el tipo específico para ventas
    }

    @Override
    public List<ComprobanteDetalleRequest> obtenerDetalle() {
        return comprobantesVentaDet.stream()
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
    public Long getIdTercero() {
        return idCliente;
    }
    @Override
    public List<ComprobantesVentasCuotasDTO> getCuotas() {
        return comprobantesVentasCuotas;  // Retorna la lista de cuotas específica de ventas
    }
}
