package com.microservice.inventario.persistence.repository;

import com.microservice.inventario.persistence.entity.MovimientosMotivosEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IMovimientosMotivosRepository extends JpaRepository<MovimientosMotivosEntity, String> {
}
