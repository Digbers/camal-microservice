package com.microservice.inventario.persistence.repository;

import com.microservice.inventario.persistence.entity.MovimientoSacrificioCabecera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IMovimientoSacrificioCabeceraRepository extends JpaRepository<MovimientoSacrificioCabecera, Long> {
}
