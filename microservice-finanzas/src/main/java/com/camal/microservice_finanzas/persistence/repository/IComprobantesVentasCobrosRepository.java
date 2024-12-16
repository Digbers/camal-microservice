package com.camal.microservice_finanzas.persistence.repository;

import com.camal.microservice_finanzas.controller.response.IngresosXFechaDTO;
import com.camal.microservice_finanzas.persistence.entity.ComprobantesVentasCobrosEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface IComprobantesVentasCobrosRepository extends JpaRepository<ComprobantesVentasCobrosEntity, Long> {
    @Query("SELECT c FROM ComprobantesVentasCobrosEntity c WHERE c.idComprobanteVenta = :idComprobanteVenta")
    List<ComprobantesVentasCobrosEntity> findByIdComprobanteVenta(Long idComprobanteVenta);

    @Query("SELECT new com.camal.microservice_finanzas.controller.response.IngresosXFechaDTO(c.fechaCobro, COALESCE(SUM(c.montoCobrado), 0)) " +
            "FROM ComprobantesVentasCobrosEntity c " +
            "WHERE c.idEmpresa = :idEmpresa AND c.fechaCobro BETWEEN :fechaInicio AND :fechaFin " +
            "GROUP BY c.fechaCobro " +
            "ORDER BY c.fechaCobro")
    List<IngresosXFechaDTO> findIngresosPorFecha(@Param("idEmpresa") Long idEmpresa,
                                               @Param("fechaInicio") LocalDate fechaInicio,
                                               @Param("fechaFin") LocalDate fechaFin);


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
