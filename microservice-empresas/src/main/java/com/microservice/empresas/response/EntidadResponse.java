package com.microservice.empresas.response;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntidadResponse {
    private Long id;
    @NotBlank(message = "El tipo de documento es obligatorio")
    private String documento;
    @NotBlank(message = "El numero de documento es obligatorio")
    private String numeroDocumento;
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    private String estado;
    private String condicion;
    private String direccion;
}
