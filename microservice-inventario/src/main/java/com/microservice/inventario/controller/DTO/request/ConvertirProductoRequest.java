package com.microservice.inventario.controller.DTO.request;

import jakarta.validation.constraints.NotNull;

public record ConvertirProductoRequest(
        @NotNull Long idEmpresa,
        @NotNull Long idAlmacen,
        @NotNull Long idProducto,
        @NotNull Long idEnvase,
        @NotNull int cantidadProducto,
        @NotNull double pesoTotal,
        @NotNull String usuCodigo,
        @NotNull Long idProductoTransformasdo,
        String observaciones,
        @NotNull double peso
) {
}
