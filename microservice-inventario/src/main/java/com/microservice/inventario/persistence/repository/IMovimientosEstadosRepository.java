package com.microservice.inventario.persistence.repository;

import com.microservice.inventario.persistence.entity.MovimientosEstadosEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IMovimientosEstadosRepository extends JpaRepository<MovimientosEstadosEntity, Long> {
    MovimientosEstadosEntity findByCodigo(String codigo);
}
