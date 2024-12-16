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
public class GastosXFechaDTO {
    private LocalDate fechaPago;
    private BigDecimal gastoTotal;
}
