package com.microservice.empresas.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateEntidadRequest {
    @NotNull(message = "El campo idEmpresa es obligatorio")
    private Long idEmpresa;
    private Long id;
    private Long zona;
    @NotBlank(message = "El campo nombre es obligatorio")
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    @NotBlank(message = "El campo documentoTipo es obligatorio")
    private String documentoTipo;
    @NotBlank(message = "El campo nroDocumento es obligatorio")
    private String nroDocumento;
    private String email;
    private String celular;
    private String direccion;
    private String sexo;
    private Boolean estado;
    private String condicion;
    private Double sueldo;
    private String usuarioCreacion;
    private String usuarioActualizacion;
    @Valid
    @NotNull(message = "El campo entidadesTipos es obligatorio")
    private List<String> entidadesTipos;
}
