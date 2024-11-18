package com.microservice.inventario.controller;

import com.microservice.inventario.service.productos.ProductosServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/product-free-pass")
@RequiredArgsConstructor
public class ProductControllerFreePass {
    private final ProductosServiceImpl productosService;

    @GetMapping("/stream-stock-product-venta/{codigo}")
    public SseEmitter streamData(@PathVariable String codigo) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        // Ejecutar la tarea en un método asíncrono
        productosService.executeStream(emitter, codigo);

        return emitter;
    }
}
