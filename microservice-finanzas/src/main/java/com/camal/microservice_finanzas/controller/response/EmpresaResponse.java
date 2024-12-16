package com.camal.microservice_finanzas.controller.response;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpresaResponse {
    private Long id;
    private String razonSocial;
    private String empresaCodigo;
    private String ruc;
    private String direccion;
    private String telefono;
    private String celular;
    private String correo;
}
