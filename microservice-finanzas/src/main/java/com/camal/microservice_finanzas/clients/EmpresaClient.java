package com.camal.microservice_finanzas.clients;

import com.camal.microservice_finanzas.controller.DTO.EmpresaDTO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class EmpresaClient {

    private final WebClient.Builder webClientBuilder;

    public EmpresaClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public Boolean verificarEmpresaExiste(Long empresaId) {
        String url = "/empresas/find/" + empresaId;

        return webClientBuilder.build()
                .get()
                .uri("http://msvc-gateway/empresas/find/" + empresaId)
                .header("X-Internal-Request", "true")
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), response -> {
                    if (response.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        return Mono.error(new RuntimeException("Empresa no encontrada"));
                    }
                    return Mono.error(new RuntimeException("Error en la solicitud: " + response.statusCode()));
                })
                .onStatus(status -> status.is5xxServerError(),
                        response -> Mono.error(new RuntimeException("Error en el servicio de empresas")))
                .bodyToMono(EmpresaDTO.class)
                .map(empresa -> true)
                .doOnNext(empresa -> {
                    System.out.println("Respuesta de la empresa: " + empresa);
                })
                .switchIfEmpty(Mono.just(false))
                .doOnError(e -> System.out.println("Error al verificar empresa: " + e.getMessage()))
                .block();
    }


    public Mono<EmpresaDTO> obtenerDetallesEmpresa(Long empresaId) {
        String url = "/empresas/find/" + empresaId;

        return webClientBuilder.build()
                .get()
                .uri("http://msvc-gateway" + url)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), response -> {
                    if (response.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        return Mono.error(new RuntimeException("Empresa no encontrada"));
                    }
                    return Mono.error(new RuntimeException("Error en la solicitud: " + response.statusCode()));
                })
                .onStatus(status -> status.is5xxServerError(),
                        response -> Mono.error(new RuntimeException("Error en el servicio de empresas")))
                .bodyToMono(EmpresaDTO.class)
                .doOnError(e -> System.out.println("Error al obtener detalles de la empresa: " + e.getMessage()));
    }
}

