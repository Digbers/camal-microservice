package com.microservice.empresas.service;

import com.microservice.empresas.client.EntidadesSClient;
import com.microservice.empresas.controller.dto.PadronSunatDTO;
import com.microservice.empresas.request.EntidadRequest;
import com.microservice.empresas.request.ReniectRequest;
import com.microservice.empresas.response.EntidadResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EntidadesSService implements IEmpresaSService {
    private final EntidadesSClient entidadesSClient;
    @Value("${sunat.url}")
    private String urlSunat;
    @Value("${reniec.url}")
    private String urlReniec;
    @Value("${reniec.token}")
    private String tokenReniec;

    @Override
        public EntidadResponse findByNumeroDocTipoDocumento(String numeroDoc, String tipoDocumento, Long empresa) {
        PadronSunatDTO padron =entidadesSClient.obtenerEntidad(numeroDoc, tipoDocumento, urlSunat, empresa);
        //log.info("nombre entidadSService: " + padron.getRazonSocial());
        EntidadResponse entidadResponse = EntidadResponse.builder()
                .documento(tipoDocumento)
                .numeroDocumento(padron.getNumeroDoc())
                .nombre(padron.getRazonSocial())
                .direccion(padron.getDomiciloFiscal())
                .estado(padron.getEstado())
                .condicion(padron.getCondicion())
                .build();
        return entidadResponse;
    }
    @Override
    public EntidadResponse findEntidadesByDNI(ReniectRequest reniectRequest,Long empresa) {
        EntidadRequest entidad = entidadesSClient.findEntidadByDNI(reniectRequest.dni(), urlReniec, tokenReniec, empresa);
        EntidadResponse entidadResponse = EntidadResponse.builder()
                .documento("DNI")
                .numeroDocumento(entidad.numero())
                .nombre(entidad.nombre_completo())
                .direccion(entidad.direccion())
                .build();
        return entidadResponse;
    }

}
