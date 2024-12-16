package com.microservice.ventas.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PuntoVentaResponse {
    private Long id;
    private Long idEmpresa;
    private String direccion;
    private String nombre;
}
