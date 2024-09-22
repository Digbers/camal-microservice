package com.microservice.inventario.persistence.especification;

import com.microservice.inventario.persistence.entity.ProductosEntity;
import com.microservice.inventario.persistence.entity.ProductosXAlmacenEntity;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;


public class ProductosSpecifications {

    public static Specification<ProductosEntity> getProductos(Long id, Long idEmpresa, String codigo, String nombre, Long tipoId, Long almacenId) {
        return (Root<ProductosEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            Predicate predicate = cb.conjunction();

            // Filtrar por ID del producto
            if (id != null) {
                predicate = cb.and(predicate, cb.equal(root.get("id"), id));
            }

            // Filtrar por ID de empresa
            if (idEmpresa != null) {
                predicate = cb.and(predicate, cb.equal(root.get("empresaId"), idEmpresa));
            }

            // Filtrar por código de producto
            if (codigo != null && !codigo.isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("codigo"), "%" + codigo + "%"));
            }

            // Filtrar por nombre de producto
            if (nombre != null && !nombre.isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("nombre"), "%" + nombre + "%"));
            }

            // Filtrar por tipo de producto
            if (tipoId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("tipo").get("id"), tipoId));
            }

            // Filtrar por almacén
            if (almacenId != null) {
                Join<ProductosEntity, ProductosXAlmacenEntity> productosXAlmacenJoin = root.join("productosXAlmacen", JoinType.INNER);
                predicate = cb.and(predicate, cb.equal(productosXAlmacenJoin.get("almacen").get("id"), almacenId));
            }

            return predicate;
        };
    }
}

