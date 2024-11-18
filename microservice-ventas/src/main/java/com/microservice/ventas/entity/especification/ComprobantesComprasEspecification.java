package com.microservice.ventas.entity.especification;

import com.microservice.ventas.client.EmpresaClient;
import com.microservice.ventas.controller.response.IdsEntidades;
import com.microservice.ventas.entity.ComprobantesComprasCaEntity;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ComprobantesComprasEspecification {
    private final EmpresaClient empresaClient;

    public Specification<ComprobantesComprasCaEntity> getComprobantesCompras(LocalDate fechaEmision, LocalDate fechaCreacion,
                                                                             String codigoTipo, String serie, String numero,
                                                                             String numeroDoc, String nombre, BigDecimal subtotal, BigDecimal impuesto,
                                                                             BigDecimal total, String monedaCodigo, Double tipoCambio, String estadoCodigo,
                                                                             Long idPuntoVenta) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            List<Long> idClientes = new ArrayList<>();
            if(numeroDoc != null && !numeroDoc.isEmpty() || nombre != null && !nombre.isEmpty()) {
                IdsEntidades idsEntidades = empresaClient.getClientesByNumeroDocOrNombre(numeroDoc, nombre);
                if (idsEntidades != null) {
                    idClientes.addAll(idsEntidades.getIds());
                }
            }
            if (fechaEmision != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("fechaEmision"), fechaEmision));
            }
            if (fechaCreacion != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("fechaCreacion"), fechaCreacion));
            }
            if (codigoTipo != null && !codigoTipo.isEmpty()) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("comprobantesTiposEntity").get("codigo"), codigoTipo));
            }
            if (serie != null && !serie.isEmpty()) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("serie"), serie));
            }
            if (numero != null && !numero.isEmpty()) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("numero"), numero));
            }
            if (idClientes != null && !idClientes.isEmpty()) {
                predicate = criteriaBuilder.and(predicate, root.get("idProveedor").in(idClientes));
            }
            if (subtotal != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("subtotal"), subtotal));
            }
            if (impuesto != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("impuesto"), impuesto));
            }
            if (total != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("total"), total));
            }
            if (monedaCodigo != null && !monedaCodigo.isEmpty()) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("codigoMoneda"), monedaCodigo));
            }
            if (tipoCambio != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("tipoCambio"), tipoCambio));
            }
            if (estadoCodigo != null && !estadoCodigo.isEmpty()) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("comprobanteCompraEstadosEntity").get("codigo"), estadoCodigo));
            }
            if (idPuntoVenta != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("idPuntoVenta"), idPuntoVenta));
            }
            return null;
        };
    }
}
