package com.microservice.ventas.controller;

import com.microservice.ventas.service.ventas.VentasServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/free-pass")
public class VentasControllerFreePass {
    @Autowired
    private VentasServiceImpl ventasService;
    @GetMapping("/stream-numero/{serie}/{idPuntoVenta}")
    public SseEmitter streamData(@PathVariable String serie, @PathVariable Long idPuntoVenta) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        // Ejecutar la tarea en un método asíncrono
        ventasService.executeStream(emitter, serie, idPuntoVenta);

        return emitter;
    }

    /*@GetMapping("/stream-numero/{serie}/{idPuntoVenta}")
    public SseEmitter streamData(@PathVariable String serie, @PathVariable Long idPuntoVenta) {
        // Crear un SseEmitter con una duración máxima indefinida
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            try {
                String numero = ventasService.getNumeroXSerie(serie, idPuntoVenta);
                System.out.println("Numero: " + numero);
                emitter.send(numero);
            } catch (Exception e) {
                System.err.println("Error al obtener el número: " + e.getMessage());
                emitter.completeWithError(e);
            }
        }, 0, 5, TimeUnit.SECONDS);

        return emitter;
    }*/
}
