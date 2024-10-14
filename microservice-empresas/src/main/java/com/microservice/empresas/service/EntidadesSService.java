package com.microservice.empresas.service;

import com.microservice.empresas.client.EntidadesSClient;
import com.microservice.empresas.controller.dto.PadronSunatDTO;
import com.microservice.empresas.request.EntidadRequest;
import com.microservice.empresas.request.ReniectRequest;
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
    public PadronSunatDTO findByNumeroDocTipoDocumento(String numeroDoc, String tipoDocumento) {
        return entidadesSClient.obtenerEntidad(numeroDoc, tipoDocumento, urlSunat);
    }
    @Override
    public EntidadRequest findEntidadesByDNI(ReniectRequest reniectRequest,Long empresa) {
        return entidadesSClient.findEntidadByDNI(reniectRequest.dni(), urlReniec, tokenReniec);
    }

}
