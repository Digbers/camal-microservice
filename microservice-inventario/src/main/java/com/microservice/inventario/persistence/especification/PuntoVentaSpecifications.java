package com.microservice.inventario.persistence.especification;

import com.microservice.inventario.persistence.entity.PuntoVentaEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class PuntoVentaSpecifications {
    public static Specification<PuntoVentaEntity> getPuntoVenta(String direccion, String nombre, Long idEmpresa) {
        return (Root<PuntoVentaEntity> root, CriteriaQuery<?> query, CriteriaBuilder builder) -> {
            Predicate predicate = builder.conjunction();
            // Filtrar por direccion
            if (direccion != null && !direccion.isEmpty()) {
                predicate = builder.and(predicate, builder.like(root.get("direccion"), "%" + direccion + "%"));
            }
            // Filtrar por nombre
            if (nombre != null && !nombre.isEmpty()) {
                predicate = builder.and(predicate, builder.like(root.get("nombre"), "%" + nombre + "%"));
            }
            // Filtrar por idEmpresa
            if (idEmpresa != null) {
                predicate = builder.and(predicate, builder.equal(root.get("idEmpresa"), idEmpresa));
            }
            return predicate;
        };
    }
}
