package com.microservice.ventas.repository;

import com.microservice.ventas.entity.ComprobantesTiposVentasEntity;
import com.microservice.ventas.entity.SeriesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ISeriesRepository extends JpaRepository<SeriesEntity, Long> {
    @Query("SELECT s FROM SeriesEntity s WHERE s.codigoTipoComprobante.codigo = :codigoTipoComprobante AND s.idPuntoVenta = :idPuntoVenta")
    List<SeriesEntity> findByCodigoTipoComprobanteCodigo(String codigoTipoComprobante,Long idPuntoVenta);

}
