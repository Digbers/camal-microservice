package com.microservice.empresas.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class EmpresaResponseDTO {
    @NotNull(message = "El ID de la empresa es obligatorio")
    private Long id;
    @NotBlank(message = "El razón social de la empresa no puede estar vacío")
    private String razonSocial;
    @NotBlank(message = "El código de la empresa no puede estar vacío")
    private String empresaCodigo;
}
