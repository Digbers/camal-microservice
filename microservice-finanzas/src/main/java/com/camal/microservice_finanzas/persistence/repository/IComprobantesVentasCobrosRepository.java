package com.camal.microservice_finanzas.persistence.repository;

import com.camal.microservice_finanzas.persistence.entity.ComprobantesVentasCobrosEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface IComprobantesVentasCobrosRepository extends JpaRepository<ComprobantesVentasCobrosEntity, Long> {
    @Query("""
        SELECT c.idComprobanteVenta as comprobanteId, 
               COALESCE(SUM(c.montoCobrado), 0) as totalCobrado
        FROM ComprobantesVentasCobrosEntity c
        WHERE c.idComprobanteVenta IN :idsComprobantes
        GROUP BY c.idComprobanteVenta
    """)
    List<Object[]> findCobrosPorComprobante(@Param("idsComprobantes") List<Long> idsComprobantes);

    default Map<Long, BigDecimal> findTotalCobrosPorComprobante(List<Long> idsComprobantes) {
        return findCobrosPorComprobante(idsComprobantes).stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (BigDecimal) row[1]
                ));
    }
}
