package com.microservice.ventas.controller.DTO.compras;

import com.microservice.ventas.event.ComprobantesComprasPagosEventDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraRequest {
    @NotNull(message = "La cabecera del documento es obligatoria")
    @Valid
    private ComprobantesComprasCaDTO comprobantesComprasCa;//agregue el valid
    @NotNull(message = "El codigo de estado es obligatorio")
    private String codigoEstado;
    @NotNull(message = "El id de la almacen es obligatorio")
    private Long idAlmacen;
    private Boolean generarMovimiento;
    @Valid
    private List<ComprobantesComprasPagosEventDTO> comprobantesComprasPagos;
    private String codigoProductoCompra;
}
