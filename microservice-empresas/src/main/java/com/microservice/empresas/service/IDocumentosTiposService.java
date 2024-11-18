package com.microservice.empresas.service;

import com.microservice.empresas.controller.dto.DocumentosTiposDTO;
import com.microservice.empresas.persistence.entity.DocumentoTiposEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IDocumentosTiposService {
    Page<DocumentosTiposDTO> findAllByEmpresa(String docCodigo, String descripcion,String codigoSunat, Pageable pageable, Long idEmpresa);
    List<DocumentosTiposDTO> findAll();

    Optional<DocumentosTiposDTO> findById(String docCodigo, Long empresa);

    DocumentosTiposDTO save(DocumentosTiposDTO documentosTiposDTO);

    void deleteById(String docCodigo, Long empresa);

    DocumentosTiposDTO update(String docCodigo, Long empresa, DocumentosTiposDTO documentosTiposDTO);
    List<DocumentosTiposDTO> findByIdEmpresa(Long empresa);
}
