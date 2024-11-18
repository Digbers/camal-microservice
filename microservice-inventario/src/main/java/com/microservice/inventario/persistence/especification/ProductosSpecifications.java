package com.microservice.inventario.persistence.especification;

import com.microservice.inventario.persistence.entity.ProductosEntity;
import com.microservice.inventario.persistence.entity.StockAlmacen;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;


public class ProductosSpecifications {

    public static Specification<ProductosEntity> getProductos(Long id, Long idEmpresa, String codigo, String nombre, String codigoTipo, Long almacenId) {
        return (Root<ProductosEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            Predicate predicate = cb.conjunction();
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
            if (codigoTipo != null) {
                predicate = cb.and(predicate, cb.equal(root.get("tipo").get("codigo"), codigoTipo));
            }

            // Filtrar por almacén
            if (almacenId != null) {
                Join<ProductosEntity, StockAlmacen> productosXStock = root.join("stockAlmacenList", JoinType.INNER);
                predicate = cb.and(predicate, cb.equal(productosXStock.get("almacen").get("id"), almacenId));
            }

            return predicate;
        };
    }
}

