package com.microservice.empresas.persistence.especification;

import com.microservice.empresas.controller.dto.DocumentosTiposDTO;
import com.microservice.empresas.persistence.entity.DocumentoTiposEntity;
import com.microservice.empresas.persistence.entity.EntidadEntity;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

public class EntidadesEspecification {
    public static Specification<EntidadEntity> getEntidades(String nombre, String apellidoPaterno, String apellidoMaterno, String documentoTipo, String nroDocumento, String email, String celular, String direccion, String sexo, Boolean estado, String condicion, Long idEmpresa) {
        return (Root <EntidadEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            Predicate predicate = cb.conjunction();
            // Filtrar por nombre
            if (nombre != null && !nombre.isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("nombre"), "%" + nombre + "%"));
            }
            // Filtrar por apellidoPaterno
            if (apellidoPaterno != null && !apellidoPaterno.isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("apellidoPaterno"), "%" + apellidoPaterno + "%"));
            }
            // Filtrar por apellidoMaterno
            if (apellidoMaterno != null && !apellidoMaterno.isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("apellidoMaterno"), "%" + apellidoMaterno + "%"));
            }
            // Filtrar por documentoTipo
            if (documentoTipo != null && !documentoTipo.isEmpty()) {
                Join<EntidadEntity, DocumentoTiposEntity> entidadXDocumentoTipo = root.join("documentoTipo", JoinType.INNER);
                predicate = cb.and(predicate, cb.equal(entidadXDocumentoTipo.get("docCodigo"), documentoTipo));
            }
            // Filtrar por nroDocumento
            if (nroDocumento != null && !nroDocumento.isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("nroDocumento"), "%" + nroDocumento + "%"));
            }
            // Filtrar por email
            if (email != null && !email.isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("email"), "%" + email + "%"));
            }
            // Filtrar por celular
            if (celular != null && !celular.isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("celular"), "%" + celular + "%"));
            }
            // Filtrar por direccion
            if (direccion != null && !direccion.isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("direccion"), "%" + direccion + "%"));
            }
            // Filtrar por sexo
            if (sexo != null && !sexo.isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("sexo"), "%" + sexo + "%"));
            }
            // Filtrar por estado
            if (estado != null) {
                predicate = cb.and(predicate, cb.equal(root.get("estado"), estado));
            }
            // Filtrar por condicion
            if (condicion != null && !condicion.isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("condicion"), "%" + condicion + "%"));
            }
            if (idEmpresa != null) {
                predicate = cb.and(predicate, cb.equal(root.get("empresa").get("id"), idEmpresa));
            }
            return predicate;
        };
    }
}
