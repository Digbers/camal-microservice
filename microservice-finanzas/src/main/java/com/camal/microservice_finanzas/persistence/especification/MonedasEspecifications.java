package com.camal.microservice_finanzas.persistence.especification;

import com.camal.microservice_finanzas.persistence.entity.MonedasEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class MonedasEspecifications {
    public static Specification<MonedasEntity> getMonedas(String codigo, String nombre, String simbolo, Long idEmpresa) {
        return (Root<MonedasEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            Predicate predicate = cb.conjunction();
            // Filtrar por código de moneda
            if (codigo != null && !codigo.isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("codigo"), "%" + codigo + "%"));
            }
            // Filtrar por nombre de moneda
            if (nombre != null && !nombre.isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("nombre"), "%" + nombre + "%"));
            }
            // Filtrar por simbolo de moneda
            if (simbolo != null && !simbolo.isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("simbolo"), "%" + simbolo + "%"));
            }
            if (idEmpresa != null) {
                predicate = cb.and(predicate, cb.equal(root.get("idEmpresa"), idEmpresa));
            }
            return predicate;
        };
    }
}
