package com.microservice.inventario.persistence.especification;

import com.microservice.inventario.persistence.entity.UnidadesEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class UnidadesEspecification {
    public static Specification<UnidadesEntity> getUnidades(String codigo, String nombre, Long idEmpresa) {
        return (Root<UnidadesEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            Predicate predicate = cb.conjunction();
            // Filtrar por nombre
            if (codigo != null && !codigo.isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("codigo"), "%" + codigo + "%"));
            }
            // Filtrar por apellidoPaterno
            if (nombre != null && !nombre.isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("nombre"), "%" + nombre + "%"));
            }
            if (idEmpresa != null) {
                predicate = cb.and(predicate, cb.equal(root.get("idEmpresa"), idEmpresa));
            }
            return predicate;
        };
    }
}
