package com.microservice.inventario.persistence.especification;

import com.microservice.inventario.persistence.entity.EnvaseEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class EnvaseSpecifications {

    public static Specification<EnvaseEntity> getEnvase(Long idEnvase, Long idEmpresa, String tipoEnvase,String descripcion, Integer capacidad, Double pesoReferencia, String estado) {
        return (Root<EnvaseEntity> root, CriteriaQuery<?> query, CriteriaBuilder builder) -> {
            Predicate predicate = builder.conjunction(); // Crea un predicado inicial (siempre verdadero)
            // 1. Filtrar por idEnvase
            if (idEnvase != null) {
                predicate = builder.and(predicate, builder.equal(root.get("idEnvase"), idEnvase));
            }
            // 2. Filtrar por idEmpresa
            if (idEmpresa != null) {
                predicate = builder.and(predicate, builder.equal(root.get("idEmpresa"), idEmpresa));
            }
            // 3. Filtrar por tipoEnvase
            if (tipoEnvase != null && !tipoEnvase.isEmpty()) {
                predicate = builder.and(predicate, builder.like(root.get("tipoEnvase"), "%" + tipoEnvase + "%"));
            }
            // 4. Filtrar por descripcion
            if (descripcion != null && !descripcion.isEmpty()) {
                predicate = builder.and(predicate, builder.like(root.get("descripcion"), "%" + descripcion + "%"));
            }
            // 4. Filtrar por capacidad (cantidad de pollos por envase)
            if (capacidad != null) {
                predicate = builder.and(predicate, builder.equal(root.get("capacidad"), capacidad));
            }
            // 5. Filtrar por pesoReferencia
            if (pesoReferencia != null) {
                predicate = builder.and(predicate, builder.equal(root.get("pesoReferencia"), pesoReferencia));
            }
            // 6. Filtrar por estado
            if (estado != null && !estado.isEmpty()) {
                predicate = builder.and(predicate, builder.equal(root.get("estado"), estado));
            }

            return predicate;
        };
    }
}