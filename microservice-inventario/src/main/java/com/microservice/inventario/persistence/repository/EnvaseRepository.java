package com.microservice.inventario.persistence.repository;

import com.microservice.inventario.persistence.entity.EnvaseEntity;
import com.microservice.inventario.persistence.especification.EnvaseSpecifications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EnvaseRepository extends JpaRepository<EnvaseEntity, Long>, JpaSpecificationExecutor<EnvaseEntity> {
    @Query("SELECT e FROM EnvaseEntity e JOIN e.stockAlmacenList s WHERE s.almacen.idAlmacen = :idAlmacen")
    List<EnvaseEntity> findEnvasesByAlmacen(@Param("idAlmacen") Long idAlmacen);
}
