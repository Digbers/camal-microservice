package com.microservice.empresas.persistence.repository;

import com.microservice.empresas.persistence.entity.DocumentoTiposEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IDocumentosTiposRepository extends JpaRepository<DocumentoTiposEntity, Long>, JpaSpecificationExecutor<DocumentoTiposEntity> {

    @Query("SELECT DISTINCT d.codigoSunat FROM DocumentoTiposEntity d WHERE d.docCodigo = :docCodigo")
    List<String> findDistinctCodigoSunatByDocCodigo(@Param("docCodigo") String docCodigo);
    @Query("SELECT d FROM DocumentoTiposEntity d WHERE d.empresa.id = :empresa")
    List<DocumentoTiposEntity> findByEmpresaId(Long empresa);
    @Query("SELECT d FROM DocumentoTiposEntity d WHERE d.empresa.id = :empresa AND d.docCodigo = :docCodigo")
    Optional<DocumentoTiposEntity> findByEmpresaAndDocCodigo(Long empresa, String docCodigo);

}
