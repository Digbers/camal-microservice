package com.microservice.empresas.service;

import com.microservice.empresas.controller.dto.PadronSunatDTO;
import com.microservice.empresas.request.EntidadRequest;
import com.microservice.empresas.request.ReniectRequest;

public interface IEmpresaSService {
    PadronSunatDTO findByNumeroDocTipoDocumento(String numeroDoc, String tipoDocumento);
    EntidadRequest findEntidadesByDNI(ReniectRequest reniectRequest, Long empresa);
}
