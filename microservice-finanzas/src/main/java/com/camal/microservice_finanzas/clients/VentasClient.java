package com.camal.microservice_finanzas.clients;

import com.camal.microservice_finanzas.controller.DTO.RestResponse;
import com.camal.microservice_finanzas.controller.response.ComprobanteVentaResponseDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Component
public class VentasClient {

    private final WebClient.Builder webClientBuilder;

    public VentasClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }
    public Page<ComprobanteVentaResponseDTO> getComprobantesFromVentas(String comprobanteTipo, String serie, String numero, String numeroDoc, String nombre, String monedaCodigo, BigDecimal total, BigDecimal pagado, BigDecimal saldo, Pageable pageable) {
        return webClientBuilder.build()
                .get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/api/ventas/comprobantes/reduced")
                            .queryParam("page", pageable.getPageNumber())
                            .queryParam("size", pageable.getPageSize())
                            .queryParam("sort", getSortParams(pageable));

                    if (comprobanteTipo != null) uriBuilder.queryParam("comprobanteTipo", comprobanteTipo);
                    if (serie != null) uriBuilder.queryParam("serie", serie);
                    if (numero != null) uriBuilder.queryParam("numero", numero);
                    if (numeroDoc != null) uriBuilder.queryParam("numeroDoc", numeroDoc);
                    if (nombre != null) uriBuilder.queryParam("nombre", nombre);
                    if (monedaCodigo != null) uriBuilder.queryParam("monedaCodigo", monedaCodigo);
                    if (total != null) uriBuilder.queryParam("total", total);

                    return uriBuilder.build();
                })
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<RestResponse<Page<ComprobanteVentaResponseDTO>>>() {})
                .map(RestResponse::getData)
                .block();
    }

    private String getSortParams(Pageable pageable) {
        return pageable.getSort()
                .stream()
                .map(order -> order.getProperty() + "," + order.getDirection())
                .collect(Collectors.joining(","));
    }

}
