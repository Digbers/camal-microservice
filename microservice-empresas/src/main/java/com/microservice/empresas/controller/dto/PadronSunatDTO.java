package com.microservice.empresas.controller.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PadronSunatDTO {
    private String numeroDoc;
    private String razonSocial;
    private String estado;
    private String condicion;
    private String domiciloFiscal;
}
