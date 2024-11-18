package com.microservice.empresas.service;

import com.microservice.empresas.request.ReniectRequest;
import com.microservice.empresas.response.EntidadResponse;

public interface IEmpresaSService {
    EntidadResponse findByNumeroDocTipoDocumento(String numeroDoc, String tipoDocumento, Long empresa);
    EntidadResponse findEntidadesByDNI(ReniectRequest reniectRequest, Long empresa);
}
