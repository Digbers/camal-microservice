package com.microservice.inventario.persistence.repository;

import com.microservice.inventario.persistence.entity.ProductosEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductosRepository extends JpaRepository<ProductosEntity, Long>, JpaSpecificationExecutor<ProductosEntity> {
    @Query("SELECT p FROM ProductosEntity p LEFT JOIN FETCH p.productosXAlmacenes WHERE p.id = :id")
    Optional<ProductosEntity> findByIdWithAlmacenes(@Param("id") Long id);
    @Query("SELECT DISTINCT p FROM ProductosEntity p " +
            "JOIN FETCH p.productosXAlmacenes pa " +
            "WHERE pa.cantidad < 10")
    List<ProductosEntity> findStockMinimo();

    Optional<ProductosEntity> findByCodigo(String codigo);

    @Query("SELECT p FROM ProductosEntity p " +
            "LEFT JOIN p.tipo t " +
            "LEFT JOIN p.productosXAlmacenes px " +
            "LEFT JOIN px.almacen a " +
            "WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(t.nombre) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(a.nombre) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<ProductosEntity> searchByFields(@Param("searchTerm") String searchTerm);


}
