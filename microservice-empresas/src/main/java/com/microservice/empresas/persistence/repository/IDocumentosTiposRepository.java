package com.microservice.empresas.persistence.repository;

import com.microservice.empresas.persistence.entity.DocumentoTiposEntity;
import com.microservice.empresas.persistence.entity.ids.DocumentosTiposId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IDocumentosTiposRepository extends CrudRepository<DocumentoTiposEntity, DocumentosTiposId> {

    @Query("SELECT DISTINCT d.codigoSunat FROM DocumentoTiposEntity d WHERE d.docCodigo = :docCodigo")
    List<String> findDistinctCodigoSunatByDocCodigo(@Param("docCodigo") String docCodigo);

}
