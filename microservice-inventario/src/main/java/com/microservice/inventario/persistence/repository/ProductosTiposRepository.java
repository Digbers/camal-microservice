package com.microservice.inventario.persistence.repository;

import com.microservice.inventario.persistence.entity.ProductosTiposEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface ProductosTiposRepository extends JpaRepository<ProductosTiposEntity, Long>, JpaSpecificationExecutor<ProductosTiposEntity> {
    Optional<ProductosTiposEntity> findByCodigo(String codigo);
    List<ProductosTiposEntity> findByIdEmpresa(Long idEmpresa);
    Optional<ProductosTiposEntity> findByIdEmpresaAndCodigo(Long idEmpresa, String codigo);

}
