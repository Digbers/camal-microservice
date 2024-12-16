package com.microservice.ventas.controller.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonedasRequest {
    private Long id;
    private String codigo;
    private Long idEmpresa;
    private String nombre;
    private String simbolo;
}
