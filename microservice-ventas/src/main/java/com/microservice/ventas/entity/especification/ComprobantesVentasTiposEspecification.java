package com.microservice.ventas.entity.especification;

import com.microservice.ventas.entity.ComprobantesTiposVentasEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class ComprobantesVentasTiposEspecification {
    public static Specification<ComprobantesTiposVentasEntity> getComprobantesVentasTipos(String codigo, String descripcion, Long idEmpresa) {
        return (Root<ComprobantesTiposVentasEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            Predicate predicate = cb.conjunction();
            // Filtrar por nombre
            if (codigo != null && !codigo.isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("codigo"), "%" + codigo + "%"));
            }
            // Filtrar por apellidoPaterno
            if (descripcion != null && !descripcion.isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("descripcion"), "%" + descripcion + "%"));
            }
            if (idEmpresa != null) {
                predicate = cb.and(predicate, cb.equal(root.get("idEmpresa"), idEmpresa));
            }
            return predicate;
        };
    }
}
