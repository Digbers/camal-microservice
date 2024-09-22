package com.microservice.empresas.service;

import com.microservice.empresas.controller.dto.DocumentosTiposDTO;
import com.microservice.empresas.persistence.entity.DocumentoTiposEntity;

import java.util.List;
import java.util.Optional;

public interface IDocumentosTiposService {
    List<DocumentoTiposEntity> findAll();

    Optional<DocumentoTiposEntity> findById(String docCodigo, Long empresa);

    DocumentoTiposEntity save(DocumentosTiposDTO documentosTiposDTO);

    void deleteById(String docCodigo, Long empresa);

    DocumentoTiposEntity update(String docCodigo, Long empresa, DocumentosTiposDTO documentosTiposDTO);
}
