package com.microservice.inventario.persistence.repository;

import com.microservice.inventario.persistence.entity.ProductosXAlmacenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IProductosXAlmacenRepository extends JpaRepository<ProductosXAlmacenEntity, Long> {
    //ProductosXAlmacenEntity findByProductoIdAndAlmacenId(Long productoId, Long almacenId);
    ProductosXAlmacenEntity findByProductosIdAndAlmacenId(Long productoId, Long almacenId);
}