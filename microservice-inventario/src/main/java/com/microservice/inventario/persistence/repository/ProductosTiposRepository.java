package com.microservice.inventario.persistence.repository;

import com.microservice.inventario.persistence.entity.ProductosTiposEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductosTiposRepository extends JpaRepository<ProductosTiposEntity, String> {
    Optional<ProductosTiposEntity> findByCodigo(String codigo);
}
