package com.camal.microservice_finanzas.clients;

import com.camal.microservice_finanzas.controller.DTO.EmpresaDTO;
import com.camal.microservice_finanzas.controller.response.EmpresaResponse;
import com.camal.microservice_finanzas.controller.response.IdsEntidades;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
@Slf4j
public class EmpresaClient {

    private final WebClient.Builder webClientBuilder;

    public EmpresaClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public EmpresaResponse obtenerDetallesEmpresa(Long empresaId) {
        log.info("Obteniendo detalles empresa con id: " + empresaId);
        try {
            return webClientBuilder.build()
                    .get()
                    .uri("http://msvc-gateway/api/empresas/find/" + empresaId)
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
                    .bodyToMono(EmpresaResponse.class)
                    .doOnError(e -> System.out.println("Error al verificar empresa: " + e.getMessage()))
                    .block();
        } catch (Exception e) {
            log.error("Error al buscar empresa: " + e.getMessage());
            throw new RuntimeException("Error al buscar empresa: " + e.getMessage());
        }
    }

    public IdsEntidades getClientesByNumeroDocOrNombre(String numeroDoc, String nombre) {
        try {
            return webClientBuilder.build()
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("http://msvc-gateway/api/empresas/entidades/search")
                            .queryParamIfPresent("nroDocumento", Optional.ofNullable(numeroDoc))
                            .queryParamIfPresent("nombre", Optional.ofNullable(nombre))
                            .build())
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
                    .bodyToMono(IdsEntidades.class) // Cambiado a bodyToMono
                    .block(); // Bloquea hasta que se reciba la respuesta
        } catch (Exception e) {
            log.error("Error al buscar clientes por numeroDoc y nombre: " + e.getMessage());
            log.error("Stack Trace: ", e);
            return null; // Retorna null en caso de error
        }
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

}

