package com.microservice.ventas.client;

import com.microservice.ventas.config.WebClientConfig;
import com.microservice.ventas.controller.DTO.EmpresaDTO;
import com.microservice.ventas.controller.request.PuntoVentaResponse;
import com.microservice.ventas.exception.ProductoNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class InventarioClient {

    private final WebClient.Builder webClientBuilder;

    public InventarioClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public PuntoVentaResponse obtenerDetallesPuntoVenta(Long idPuntoVenta) {
        log.info("Obteniendo detalles del punto de venta con ID: " + idPuntoVenta);
        try {
            return webClientBuilder.build()
                    .get()
                    .uri("http://msvc-gateway/api/inventario/punto-venta/find/" + idPuntoVenta)
                    .header("X-Internal-Request", "true")
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(), response -> {
                        if (response.statusCode().equals(HttpStatus.NOT_FOUND)) {
                            return Mono.error(new RuntimeException("Punto de venta no encontrado"));
                        }
                        return Mono.error(new RuntimeException("Error en la solicitud: " + response.statusCode()));
                    })
                    .onStatus(status -> status.is5xxServerError(),
                            response -> Mono.error(new RuntimeException("Error en el servicio de inventario")))
                    .bodyToMono(PuntoVentaResponse.class)
                    .doOnError(e -> System.out.println("Error al verificar inventario: " + e.getMessage()))
                    .block();
        } catch (Exception e) {
            log.error("Error al buscar punto de venta: " + e.getMessage());
            throw new RuntimeException("Error al buscar punto de venta: " + e.getMessage());
        }
    }

    public Integer obtenerStockDisponible(Long id) {
        String url = "/inventarios/verificar-stock/" + id;
        try {
            return webClientBuilder.build()
                    .get()
                    .uri("http://msvc-gateway" + url)
                    .header("X-Internal-Request", "true")
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(), response -> {
                        if (response.statusCode().equals(HttpStatus.NOT_FOUND)) {
                            return response.bodyToMono(String.class)
                                    .flatMap(errorMessage -> Mono.error(new ProductoNotFoundException(errorMessage)));
                        }
                        return Mono.error(new RuntimeException("Error en la solicitud: " + response.statusCode()));
                    })
                    .onStatus(status -> status.is5xxServerError(),
                            response -> Mono.error(new RuntimeException("Error en el servicio de empresas")))
                    .bodyToMono(Integer.class)
                    .block();  // Espera la respuesta sincrónicamente
        } catch (ProductoNotFoundException ex) {
            throw new ProductoNotFoundException("Producto no encontrado: " + id);
        } catch (Exception ex) {
            throw new RuntimeException("Error al obtener stock del producto ID: " + id, ex);
        }
    }
}
