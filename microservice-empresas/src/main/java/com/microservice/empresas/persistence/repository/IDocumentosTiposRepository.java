package com.microservice.empresas.persistence.repository;

import com.microservice.empresas.persistence.entity.DocumentoTiposEntity;
import com.microservice.empresas.persistence.entity.ids.DocumentosTiposId;
import org.springframework.data.repository.CrudRepository;

public interface IDocumentosTiposRepository extends CrudRepository<DocumentoTiposEntity, DocumentosTiposId> {
}
