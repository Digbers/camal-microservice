package com.microservice.inventario.persistence.repository;

import com.microservice.inventario.persistence.entity.AlmacenEntity;
import com.microservice.inventario.persistence.entity.PuntoVentaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IPuntoVentaRepository extends JpaRepository<PuntoVentaEntity, Long>, JpaSpecificationExecutor<PuntoVentaEntity> {
    @Query("SELECT u FROM PuntoVentaEntity u WHERE u.almacen.id = ?1")
    List<PuntoVentaEntity> findAllByIdAlmacen(Long almacenId);
    @Query("SELECT u FROM PuntoVentaEntity u WHERE u.almacen.id = :id")
    Optional<AlmacenEntity> findByIdAlmacen(@Param("id") Long almacenId);


}
