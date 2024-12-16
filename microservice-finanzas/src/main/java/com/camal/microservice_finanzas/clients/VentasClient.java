package com.camal.microservice_finanzas.clients;

import com.camal.microservice_finanzas.controller.DTO.PageResponse;
import com.camal.microservice_finanzas.controller.DTO.RestResponse;
import com.camal.microservice_finanzas.controller.DTO.compras.ComprobantesComprasCaDTO;
import com.camal.microservice_finanzas.controller.DTO.ventas.ComprobantesVentasCabDTO;
import com.camal.microservice_finanzas.controller.response.ComprobanteVentaResponseDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.stream.Collectors;

@Component
public class VentasClient {

    private final WebClient.Builder webClientBuilder;

    public VentasClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }
    public Page<ComprobantesVentasCabDTO> getComprobantesFromVentas(
            LocalDate fechaEmision, String comprobanteTipo, String serie, String numero,
            String numeroDoc, String nombre, String monedaCodigo,
            BigDecimal total, BigDecimal pagado, BigDecimal saldo, Pageable pageable) {

        PageResponse<ComprobantesVentasCabDTO> response = webClientBuilder.build()
                .get()
                .uri(uriBuilder -> {
                    uriBuilder = UriComponentsBuilder
                            .fromHttpUrl("http://msvc-gateway/api/ventas/comprobantes/list")
                            .queryParam("page", pageable.getPageNumber())
                            .queryParam("size", pageable.getPageSize())
                            .queryParam("sort", getSortParams(pageable));

                    if (fechaEmision != null) uriBuilder.queryParam("fechaEmision", fechaEmision);
                    if (comprobanteTipo != null) uriBuilder.queryParam("codigoTipo", comprobanteTipo);
                    if (serie != null) uriBuilder.queryParam("serie", serie);
                    if (numero != null) uriBuilder.queryParam("numero", numero);
                    if (numeroDoc != null) uriBuilder.queryParam("numeroDoc", numeroDoc);
                    if (nombre != null) uriBuilder.queryParam("nombre", nombre);
                    if (monedaCodigo != null) uriBuilder.queryParam("monedaCodigo", monedaCodigo);
                    if (total != null) uriBuilder.queryParam("total", total);

                    return uriBuilder.build();
                })
                .header("X-Internal-Request", "true")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<PageResponse<ComprobantesVentasCabDTO>>() {})
                .block();

        return new PageImpl<>(
                response.getContent(),
                pageable,
                response.getTotalElements()
        );
    }


    public Page<ComprobantesComprasCaDTO> getComprobantesFromCompras(
            LocalDate fechaEmision, String comprobanteTipo, String serie, String numero,
            String numeroDoc, String nombre, String monedaCodigo,
            BigDecimal total, BigDecimal pagado, BigDecimal saldo, Pageable pageable) {

        PageResponse<ComprobantesComprasCaDTO> response = webClientBuilder.build()
                .get()
                .uri(uriBuilder -> {
                    uriBuilder = UriComponentsBuilder
                            .fromHttpUrl("http://msvc-gateway/api/compras/comprobantes/list")
                            .queryParam("page", pageable.getPageNumber())
                            .queryParam("size", pageable.getPageSize())
                            .queryParam("sort", getSortParams(pageable));

                    if (fechaEmision != null) uriBuilder.queryParam("fechaEmision", fechaEmision);
                    if (comprobanteTipo != null) uriBuilder.queryParam("codigoTipo", comprobanteTipo);
                    if (serie != null) uriBuilder.queryParam("serie", serie);
                    if (numero != null) uriBuilder.queryParam("numero", numero);
                    if (numeroDoc != null) uriBuilder.queryParam("numeroDoc", numeroDoc);
                    if (nombre != null) uriBuilder.queryParam("nombre", nombre);
                    if (monedaCodigo != null) uriBuilder.queryParam("monedaCodigo", monedaCodigo);
                    if (total != null) uriBuilder.queryParam("total", total);

                    return uriBuilder.build();
                })
                .header("X-Internal-Request", "true")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<PageResponse<ComprobantesComprasCaDTO>>() {})
                .block();

        return new PageImpl<>(
                response.getContent(),
                pageable,
                response.getTotalElements()
        );
    }


    private String getSortParams(Pageable pageable) {
        return pageable.getSort()
                .stream()
                .map(order -> order.getProperty() + "," + order.getDirection())
                .collect(Collectors.joining(","));
    }

}
