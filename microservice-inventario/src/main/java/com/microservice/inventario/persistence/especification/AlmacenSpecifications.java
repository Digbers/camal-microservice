package com.microservice.inventario.persistence.especification;

import com.microservice.inventario.persistence.entity.AlmacenEntity;
import com.microservice.inventario.persistence.entity.AlmacenTipoEntity;
import com.microservice.inventario.persistence.entity.ProductosEntity;
import com.microservice.inventario.persistence.entity.StockAlmacen;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

public class AlmacenSpecifications {
    public static Specification<AlmacenEntity> getAlmacenes(String nombre, String tipoAlmacen, Long idEmpresa) {
        return (Root<AlmacenEntity> root, CriteriaQuery<?> query, CriteriaBuilder builder) -> {
            Predicate predicate = builder.conjunction();
            // Filtrar por nombre
            if (nombre != null && !nombre.isEmpty()) {
                predicate = builder.and(predicate, builder.like(root.get("nombre"), "%" + nombre + "%"));
            }
            // Filtrar por tipoAlmacen
            if (tipoAlmacen != null && !tipoAlmacen.isEmpty()) {
                Join<AlmacenEntity, AlmacenTipoEntity> tipoAlmacenS = root.join("tipoAlmacen", JoinType.INNER);
                predicate = builder.and(predicate, builder.equal(tipoAlmacenS.get("codigo"), tipoAlmacen));
            }
            // Filtrar por idEmpresa
            if (idEmpresa != null) {
                predicate = builder.and(predicate, builder.equal(root.get("idEmpresa"), idEmpresa));
            }
            return predicate;
        };
    }
}
