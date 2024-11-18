package com.microservice.empresas.client;

import com.microservice.empresas.controller.dto.PadronSunatDTO;
import com.microservice.empresas.exception.EntidadNotFoundException;
import com.microservice.empresas.persistence.entity.EmpresaEntity;
import com.microservice.empresas.persistence.entity.EntidadEntity;
import com.microservice.empresas.persistence.entity.PadronSunat;
import com.microservice.empresas.persistence.repository.IEmpresaRepository;
import com.microservice.empresas.persistence.repository.IEntidadRepository;
import com.microservice.empresas.persistence.repository.IPadronSunatRepository;
import com.microservice.empresas.request.EntidadRequest;
import com.microservice.empresas.response.ApiResponse;
import com.microservice.empresas.service.WebScraperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Component
@RequiredArgsConstructor
@Slf4j
public class EntidadesSClient {
    private final WebScraperService webScraperService;
    private final WebClient.Builder webClientBuilder;
    private final IEmpresaRepository empresaRepository;
    private final IEntidadRepository entidadRepository;



    public PadronSunatDTO obtenerEntidad(String numeroDoc, String tipoDoc, String url, Long empresa) {
        try {
            String searchInputId = "txtRuc"; // Id del input en la página de consulta
            String searchButtonId = "btnAceptar";  // Id del botón de búsqueda
            Optional<EmpresaEntity> empresaEntity = empresaRepository.findById(empresa);
            if (empresaEntity.isEmpty()) {
                log.warn("Empresa no encontrada en la base de datos.");
                throw new RuntimeException("Empresa no encontrada en la base de datos.");
            }
            Optional<EntidadEntity> entidad = entidadRepository.findByEmpresaAndNroDocumento(empresaEntity.get(), numeroDoc);
            if (entidad.isPresent()) {
                EntidadEntity entidadEntity = entidad.get();
                log.info("Entidad encontrada en la base de datos.");
                return PadronSunatDTO.builder()
                        .numeroDoc(entidadEntity.getNroDocumento())
                        .razonSocial(entidadEntity.getNombre())
                        .estado(entidadEntity.getEstado() ? "ACTIVO" : "INACTIVO")
                        .condicion(entidadEntity.getCondicion())
                        .domiciloFiscal(entidadEntity.getDireccion())
                        .build();
            }
            return webScraperService.navigateAndExtract(url, searchInputId, searchButtonId, numeroDoc, tipoDoc);
        } catch (EntidadNotFoundException ex) {
            throw new EntidadNotFoundException("Entidad no encontrado: ");
        } catch (Exception ex) {
            throw new RuntimeException("Error al obtener la entidad: ", ex);
        }
    }
    public EntidadRequest findEntidadByDNI(String dni, String urlReniec, String tokenReniec, Long empresa) {
        try {
            Optional<EmpresaEntity> empresaEntity = empresaRepository.findById(empresa);
            if (empresaEntity.isEmpty()) {
                log.warn("Empresa no encontrada en la base de datos.");
                throw new RuntimeException("Empresa no encontrada en la base de datos.");
            }
            Optional<EntidadEntity> entidad = entidadRepository.findByEmpresaAndNroDocumento(empresaEntity.get(), dni);
            if (entidad.isPresent()) {
                log.info("Entidad encontrada en la base de datos.");
                EntidadEntity entidadEntity = entidad.get();
                String nombrecompleto = entidadEntity.getNombre() + " " + entidadEntity.getApellidoPaterno() + " " + entidadEntity.getApellidoMaterno();
                EntidadRequest entidadRequest = new EntidadRequest(entidadEntity.getNroDocumento(), nombrecompleto, entidadEntity.getNombre(),entidadEntity.getApellidoPaterno(), entidadEntity.getApellidoMaterno(), entidadEntity.getDireccion());
                return entidadRequest;
            }
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("dni", dni);
            ApiResponse response = webClientBuilder.build()
                    .post()
                    .uri(urlReniec)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenReniec)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Mono.just(requestBody), Map.class)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(), responseClient -> {
                        if (responseClient.statusCode().equals(HttpStatus.NOT_FOUND)) {
                            return responseClient.bodyToMono(String.class)
                                    .flatMap(errorMessage -> Mono.error(new EntidadNotFoundException(errorMessage)));
                        }
                        return Mono.error(new RuntimeException("Error en la solicitud: " + responseClient.statusCode()));
                    })
                    .onStatus(status -> status.is5xxServerError(),
                            responseClient -> Mono.error(new RuntimeException("Error en el servicio de empresas")))
                    .bodyToMono(ApiResponse.class)  // Mapear a la estructura ApiResponse
                    .block();
            // Si la respuesta es correcta, devolver solo el objeto `data` que contiene los campos necesarios
            return response != null && response.isSuccess() ? response.getData() : null;
        } catch (EntidadNotFoundException ex) {
            throw new EntidadNotFoundException("Entidad no encontrado: ");
        } catch (Exception ex) {
            throw new RuntimeException("Error al obtener la entidad: ", ex);
        }

    }
}
