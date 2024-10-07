package com.camal.microservice_finanzas.event;

import com.camal.microservice_finanzas.controller.DTO.ComprobanteDetalleRequest;
import com.camal.microservice_finanzas.controller.DTO.FormasDeCobrosRequest;
import com.camal.microservice_finanzas.controller.DTO.ventas.ComprobantesVentasCabDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VentaCreadaEvent {
    @NotNull(message = "La cabecera del documento es obligatoria")
    @Valid
    private ComprobantesVentasCabDTO comprobantesVentasCab;
    @NotNull(message = "El id de la almacen es obligatorio")
    private Long idAlmacen;
    @NotNull(message = "El ID de la forma de pago es obligatorio")
    @Length(min = 1, max = 3, message = "El ID de la forma de pago debe tener entre 1 y 3 caracteres")
    private String codigoFormaPago;
}
