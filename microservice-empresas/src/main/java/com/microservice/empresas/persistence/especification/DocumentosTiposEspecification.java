package com.microservice.empresas.persistence.especification;

import com.microservice.empresas.persistence.entity.DocumentoTiposEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class DocumentosTiposEspecification {
    public static Specification<DocumentoTiposEntity> getDocumentosTipos(String docCodigo, String descripcion, String codigoSunat) {
        return (Root<DocumentoTiposEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            Predicate predicate = cb.conjunction();
            // Filtrar por docCodigo
            if (docCodigo != null && !docCodigo.isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("docCodigo"), "%" + docCodigo + "%"));
            }
            // Filtrar por descripcion
            if (descripcion != null && !descripcion.isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("descripcion"), "%" + descripcion + "%"));
            }
            // Filtrar por codigoSunat
            if (codigoSunat != null && !codigoSunat.isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("codigoSunat"), "%" + codigoSunat + "%"));
            }
            return predicate;
        };
    }
}
