package com.microservice.inventario.utils;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class VerificarStock {

    @Scheduled(cron = "0 0 9 * * ?") // Ejecutar todos los días a las 9:00 AM
    public void verificarStock() {
        System.out.println("Verificando el stock...");
    }
}
