package com.camal.microservice_finanzas.controller.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteCabecera {
    private String titulo;
    private String fechaEmision;
    private String empresa;
    private String ruc;
    private String direccion;
}
