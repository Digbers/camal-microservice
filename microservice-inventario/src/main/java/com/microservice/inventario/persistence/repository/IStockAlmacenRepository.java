package com.microservice.inventario.persistence.repository;

import com.microservice.inventario.persistence.entity.StockAlmacen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IStockAlmacenRepository extends JpaRepository<StockAlmacen, Long> {

    @Query("SELECT s FROM StockAlmacen s WHERE s.almacen.id = :almacenId AND s.producto.idProducto = :productoId AND s.envase.idEnvase = :envaseId AND s.idEmpresa = :idEmpresa")
    Optional<StockAlmacen> findByIds(
            @Param("almacenId") Long almacenId,
            @Param("productoId") Long productoId,
            @Param("envaseId") Long envaseId,
            @Param("idEmpresa") Long idEmpresa);

    @Query("SELECT s FROM StockAlmacen s WHERE s.almacen.id = :almacenId AND s.producto.idProducto = :productoId AND s.idEmpresa = :idEmpresa AND (:envaseId IS NULL OR s.envase.idEnvase = :envaseId)")
    Optional<StockAlmacen> findByIdsWithOptionalEnvase(
            @Param("almacenId") Long almacenId,
            @Param("productoId") Long productoId,
            @Param("idEmpresa") Long idEmpresa,
            @Param("envaseId") Long envaseId);


}
