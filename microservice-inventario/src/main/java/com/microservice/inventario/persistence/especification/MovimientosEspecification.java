package com.microservice.inventario.persistence.especification;

import com.microservice.inventario.persistence.entity.MovimientosCabeceraEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MovimientosEspecification {

    public static Specification<MovimientosCabeceraEntity> getMovimientos(
            Long id,
            Long idEmpresa,
            Long numero,
            LocalDate fechaEmision,
            BigDecimal total,
            String motivoCodigo,
            String idUsuario,
            String monedaCodigo
    ) {
        return (Root<MovimientosCabeceraEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            Predicate predicate = cb.conjunction();

            // Filtrar por ID
            if (id != null) {
                predicate = cb.and(predicate, cb.equal(root.get("id"), id));
            }

            // Filtrar por ID de empresa
            if (idEmpresa != null) {
                predicate = cb.and(predicate, cb.equal(root.get("idEmpresa"), idEmpresa));
            }

            // Filtrar por número
            if (numero != null) {
                predicate = cb.and(predicate, cb.equal(root.get("numero"), numero));
            }

            // Filtrar por fecha de emisión
            if (fechaEmision != null) {
                predicate = cb.and(predicate, cb.equal(root.get("fechaEmision"), fechaEmision));
            }

            // Filtrar por total
            if (total != null) {
                predicate = cb.and(predicate, cb.equal(root.get("total"), total));
            }

            // Filtrar por código de motivo
            if (motivoCodigo != null && !motivoCodigo.isEmpty()) {
                predicate = cb.and(predicate, cb.equal(root.get("motivoCodigo").get("codigo"), motivoCodigo));
            }

            // Filtrar por ID de usuario
            if (idUsuario != null && !idUsuario.isEmpty()) {
                predicate = cb.and(predicate, cb.equal(root.get("idUsuario"), idUsuario));
            }

            // Filtrar por código de moneda
            if (monedaCodigo != null && !monedaCodigo.isEmpty()) {
                predicate = cb.and(predicate, cb.equal(root.get("monedaCodigo"), monedaCodigo));
            }

            return predicate;
        };
    }
}

