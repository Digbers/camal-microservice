package com.microservice.ventas.entity.especification;

import com.microservice.ventas.entity.ComprobantesComprasEstadosEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class ComprobantesComprasEstadosEspecification {
    public static Specification<ComprobantesComprasEstadosEntity> getComprobantesComprasEstados(String codigo, String descripcion, Long idEmpresa) {
        return (Root<ComprobantesComprasEstadosEntity> root, CriteriaQuery<?> query, CriteriaBuilder builder) -> {
            Predicate predicate = builder.conjunction();
            // Filtrar por codigo
            if (codigo != null && !codigo.isEmpty()) {
                predicate = builder.and(predicate, builder.like(root.get("codigo"), "%" + codigo + "%"));
            }
            // Filtrar por descripcion
            if (descripcion != null && !descripcion.isEmpty()) {
                predicate = builder.and(predicate, builder.like(root.get("descripcion"), "%" + descripcion + "%"));
            }
            // Filtrar por idEmpresa
            if (idEmpresa != null) {
                predicate = builder.and(predicate, builder.equal(root.get("idEmpresa"), idEmpresa));
            }
            return predicate;
        };
    }
}
