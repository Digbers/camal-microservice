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

    // En el repositorio de ProductosRepository
    @Query("SELECT p FROM ProductosEntity p LEFT JOIN FETCH p.stockAlmacenList WHERE p.codigo = :codigo")
    Optional<ProductosEntity> findByCodigo(@Param("codigo") String codigo);


    @Query("SELECT p FROM ProductosEntity p " +
            "LEFT JOIN p.tipo t " +
            "LEFT JOIN p.stockAlmacenList sa " +
            "LEFT JOIN sa.almacen a " +
            "WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(t.nombre) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(a.nombre) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<ProductosEntity> searchByFields(@Param("searchTerm") String searchTerm);

    @Query("SELECT p FROM ProductosEntity p LEFT JOIN FETCH p.stockAlmacenList WHERE p.idProducto = :id")
    Optional<ProductosEntity> findByIdWithStockAlmacen(@Param("id") Long id);


}
