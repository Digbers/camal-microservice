package com.microservice.inventario.persistence.especification;

import com.microservice.inventario.controller.DTO.ProductosTiposDTO;
import com.microservice.inventario.persistence.entity.ProductosTiposEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class ProductoTipoEspecification {
    public static Specification<ProductosTiposEntity> getProductosTipos(String codigo, String nombre) {
        return (Root<ProductosTiposEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            Predicate predicate = cb.conjunction();
            // Filtrar por codigo
            if (codigo != null && !codigo.isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("codigo"), "%" + codigo + "%"));
            }
            // Filtrar por nombre
            if (nombre != null && !nombre.isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("nombre"), "%" + nombre + "%"));
            }
            return predicate;
        };
    }
}
