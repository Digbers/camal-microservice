package com.microservice.inventario.controller.DTO.request;

public record ProductoRequest(
        Long idProducto,
        String codigo,
        String nombre,
        String tipo,
        Long empresa,
        Long stockAlmacenId,
        Long almacenId,
        Long envaseId,
        int cantidadEnvase,
        int cantidadProducto

) {
}
