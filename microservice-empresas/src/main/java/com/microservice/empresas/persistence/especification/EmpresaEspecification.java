package com.microservice.empresas.persistence.especification;

import com.microservice.empresas.persistence.entity.EmpresaEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class EmpresaEspecification {
    public static Specification<EmpresaEntity> getEmpresas(String razonSocial, String empresaCodigo, String ruc, String direccion, String departamento, String provincia, String distrito, String ubigeo, String telefono, String celular, String correo, String web, String logo, Boolean estado) {
        return (Root<EmpresaEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            Predicate predicate = cb.conjunction();
            // Filtrar por razonSocial
            if (razonSocial != null && !razonSocial.isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("razonSocial"), "%" + razonSocial + "%"));
            }
            // Filtrar por empresaCodigo
            if (empresaCodigo != null && !empresaCodigo.isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("empresaCodigo"), "%" + empresaCodigo + "%"));
            }
            // Filtrar por ruc
            if (ruc != null && !ruc.isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("ruc"), "%" + ruc + "%"));
            }
            // Filtrar por direccion
            if (direccion != null && !direccion.isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("direccion"), "%" + direccion + "%"));
            }
            // Filtrar por departamento
            if (departamento != null && !departamento.isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("departamento"), "%" + departamento + "%"));
            }
            // Filtrar por provincia
            if (provincia != null && !provincia.isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("provincia"), "%" + provincia + "%"));
            }
            // Filtrar por distrito
            if (distrito != null && !distrito.isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("distrito"), "%" + distrito + "%"));
            }
            // Filtrar por ubigeo
            if (ubigeo != null && !ubigeo.isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("ubigeo"), "%" + ubigeo + "%"));
            }
            // Filtrar por telefono
            if (telefono != null && !telefono.isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("telefono"), "%" + telefono + "%"));
            }
            // Filtrar por celular
            if (celular != null && !celular.isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("celular"), "%" + celular + "%"));
            }
            // Filtrar por correo
            if (correo != null && !correo.isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("correo"), "%" + correo + "%"));
            }
            // Filtrar por web
            if (web != null && !web.isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("web"), "%" + web + "%"));
            }
            // Filtrar por logo
            if (logo != null && !logo.isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("logo"), "%" + logo + "%"));
            }
            // Filtrar por estado
            if (estado != null) {
                predicate = cb.and(predicate, cb.equal(root.get("estado"), estado));
            }
            return predicate;
        };
    }
}
