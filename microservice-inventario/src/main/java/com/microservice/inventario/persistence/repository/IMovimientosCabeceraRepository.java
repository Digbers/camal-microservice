package com.microservice.inventario.persistence.repository;

import com.microservice.inventario.persistence.entity.MovimientosCabeceraEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IMovimientosCabeceraRepository extends JpaRepository<MovimientosCabeceraEntity, Long> {
    @Query("SELECT COALESCE(MAX(m.numero), 0) FROM MovimientosCabeceraEntity m")
    Long findMaxNumber();
}
