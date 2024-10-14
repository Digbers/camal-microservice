package com.microservice.inventario.clients;

import com.microservice.inventario.controller.DTO.EmpresaDTO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

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
                .bodyToMono(EmpresaDTO.class)
                .map(empresa -> true)
                .doOnNext(empresa -> {
                    System.out.println("Respuesta de la empresa: " + empresa);
                })
                .switchIfEmpty(Mono.just(false))
                .doOnError(e -> System.out.println("Error al verificar empresa: " + e.getMessage()))
                .block();
    }
    public List<EmpresaDTO> getEmpresasByIds(Set<Long> empresaIds) {
        return webClientBuilder.build()
                .post()
                .uri("http://msvc-gateway/api/empresas/findAllByIds")
                .header("X-Internal-Request", "true")
                .bodyValue(empresaIds)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), responseClient -> {
                    if (responseClient.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        return responseClient.bodyToMono(String.class)
                                .flatMap(errorMessage -> Mono.error(new RuntimeException(errorMessage)));
                    }
                    return Mono.error(new RuntimeException("Error en la solicitud: " + responseClient.statusCode()));
                })
                .onStatus(status -> status.is5xxServerError(),
                        responseClient -> Mono.error(new RuntimeException("Error en el servicio de empresas")))
                .bodyToFlux(EmpresaDTO.class)  // Mapear a una lista de EmpresaDTO
                .collectList()  // Convertir el Flux en una List
                .block();  // Bloquear para esperar la respuesta
    }

    public EmpresaDTO obtenerDetallesEmpresa(Long empresaId) {
        return webClientBuilder.build()
                .get()
                .uri("http://msvc-gateway/api/empresas/find/" + empresaId)
                .header("X-Internal-Request", "true")
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), responseClient -> {
                    if (responseClient.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        return responseClient.bodyToMono(String.class)
                                .flatMap(errorMessage -> Mono.error(new RuntimeException(errorMessage)));
                    }
                    return Mono.error(new RuntimeException("Error en la solicitud: " + responseClient.statusCode()));
                })
                .onStatus(status -> status.is5xxServerError(),
                        responseClient -> Mono.error(new RuntimeException("Error en el servicio de empresas")))
                .bodyToMono(EmpresaDTO.class)  // Mapear a la estructura Empreass
                .block();
    }
}

