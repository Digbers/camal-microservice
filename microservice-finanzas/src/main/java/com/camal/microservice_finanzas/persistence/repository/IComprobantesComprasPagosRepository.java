package com.camal.microservice_finanzas.persistence.repository;

import com.camal.microservice_finanzas.controller.response.GastosXFechaDTO;
import com.camal.microservice_finanzas.persistence.entity.ComprobantesComprasPagosEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public interface IComprobantesComprasPagosRepository extends JpaRepository<ComprobantesComprasPagosEntity, Long> {

    @Query("SELECT new com.camal.microservice_finanzas.controller.response.GastosXFechaDTO(c.fechaPago, COALESCE(SUM(c.montoPagado), 0)) " +
            "FROM ComprobantesComprasPagosEntity c " +
            "WHERE c.idEmpresa = :idEmpresa AND c.fechaPago BETWEEN :fechaInicio AND :fechaFin " +
            "GROUP BY c.fechaPago " +
            "ORDER BY c.fechaPago")
    List<GastosXFechaDTO> findGastosXFecha(@Param("idEmpresa") Long idEmpresa,
                                             @Param("fechaInicio") LocalDate fechaInicio,
                                             @Param("fechaFin") LocalDate fechaFin);


    List<ComprobantesComprasPagosEntity> findByIdComprobanteCompra(Long idComprobante);
    @Query("""
        SELECT c.idComprobanteCompra as comprobanteId, 
               COALESCE(SUM(c.montoPagado), 0) as totalPagado
        FROM ComprobantesComprasPagosEntity c
        WHERE c.idComprobanteCompra IN :idsComprobantes
        GROUP BY c.idComprobanteCompra
    """)
    List<Object[]> findPagosPorComprobante(@Param("idsComprobantes") List<Long> idsComprobantes);

    default Map<Long, BigDecimal> findTotalPagosPorComprobante(List<Long> idsComprobantes) {
        return findPagosPorComprobante(idsComprobantes).stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (BigDecimal) row[1]
                ));
    }
}
