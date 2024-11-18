package com.microservice.ventas.entity.especification;

import com.microservice.ventas.client.EmpresaClient;
import com.microservice.ventas.controller.DTO.EmpresaDTO;
import com.microservice.ventas.controller.response.IdsEntidades;
import com.microservice.ventas.entity.ComprobantesVentasCabEntity;
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
public class ComprobantesVentasEspecification {

    private final EmpresaClient empresaClient;

    public Specification<ComprobantesVentasCabEntity> getComprobantesVentas(LocalDate fechaEmision, LocalDate fechaVencimiento, String codigoTipo,
                                                                            String serie, String numero, String numeroDoc, String nombre,
                                                                            BigDecimal subtotal, BigDecimal impuesto, BigDecimal total, String monedaCodigo,
                                                                            String estadoCodigo, Long idPuntoVenta, String sunat) {
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
            if (fechaVencimiento != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("fechaVencimiento"), fechaVencimiento));
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
                predicate = criteriaBuilder.and(predicate, root.get("idCliente").in(idClientes));
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
            if (estadoCodigo != null && !estadoCodigo.isEmpty()) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("comprobantesVentasEstadoEntity").get("codigo"), estadoCodigo));
            }
            if (idPuntoVenta != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("idPuntoVenta"), idPuntoVenta));
            }
            if (sunat != null && !sunat.isEmpty()) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("facturacionElectronicaEntity").get("feeId"), sunat));
            }

            return predicate;
        };
    }
}
