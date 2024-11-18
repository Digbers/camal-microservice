package com.microservice.ventas.controller.response;

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
    //@NotNull(message = "El ID es obligatorio")
    Long id;
    @NotBlank(message = "El tipo de documento es obligatorio")
    String documento;
    @NotBlank(message = "El numero de documento es obligatorio")
    String numeroDocumento;
    @NotBlank(message = "El nombre es obligatorio")
    String nombre;
    private String estado;
    private String condicion;
    String direccion;
}
