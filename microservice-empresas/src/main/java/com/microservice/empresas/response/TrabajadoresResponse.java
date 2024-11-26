package com.microservice.empresas.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrabajadoresResponse {
    private Long id;
    private Long zona;
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String documentoTipo;
    private String nroDocumento;
    private String email;
    private String celular;
    private String direccion;
    private String sexo;
    private Boolean estado;
    private String condicion;
    private Double sueldo;
}
