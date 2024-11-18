package com.microservice.empresas.persistence.especification;

import com.microservice.empresas.persistence.entity.EmpresaEntity;
import com.microservice.empresas.persistence.entity.EntidadesTiposEntity;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

public class EntidadesTiposEspecification {
    public static Specification<EntidadesTiposEntity> getEntidadesTipos(String tipoCodigo, String descripcion, Long idEmpresa) {
        return (Root<EntidadesTiposEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            Predicate predicate = cb.conjunction();
            // Filtrar por código de entidad
            if (tipoCodigo != null && !tipoCodigo.isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("tipoCodigo"), "%" + tipoCodigo + "%"));
            }
            // Filtrar por descripción de entidad
            if (descripcion != null && !descripcion.isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("descripcion"), "%" + descripcion + "%"));
            }
            if (idEmpresa != null) {
                Join<EntidadesTiposEntity, EmpresaEntity> join = root.join("empresa");
                predicate = cb.and(predicate, cb.equal(join.get("id"), idEmpresa));
            }
            return predicate;
        };
    }
}