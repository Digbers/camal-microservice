package com.microservice.ventas.client;

import com.microservice.ventas.controller.request.MonedasRequest;
import com.microservice.ventas.controller.request.PuntoVentaResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class FinanzasClient {
    private final WebClient.Builder webClientBuilder;

    public FinanzasClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }
    public MonedasRequest obtenerMoneda(Long idEmpresa, String codigoMoneda) {
        log.info("Obteniendo moneda con idEmpresa: " + idEmpresa + " y codigoMoneda: " + codigoMoneda);
        try {
            return webClientBuilder.build()
                    .get()
                    .uri("http://msvc-gateway/api/finanzas/monedas/" + idEmpresa + "/" + codigoMoneda)
                    .header("X-Internal-Request", "true")
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(), response -> {
                        if (response.statusCode().equals(HttpStatus.NOT_FOUND)) {
                            return Mono.error(new RuntimeException("No se encontro la moneda"));
                        }
                        return Mono.error(new RuntimeException("Error en la solicitud: " + response.statusCode()));
                    })
                    .onStatus(status -> status.is5xxServerError(),
                            response -> Mono.error(new RuntimeException("Error en el servicio de finanzas")))
                    .bodyToMono(MonedasRequest.class)
                    .doOnError(e -> System.out.println("Error al verificar moneda: " + e.getMessage()))
                    .block();
        } catch (Exception e) {
            log.error("Error al buscar la moneda: " + e.getMessage());
            throw new RuntimeException("Error al buscar la moneda: " + e.getMessage());
        }
    }
}
