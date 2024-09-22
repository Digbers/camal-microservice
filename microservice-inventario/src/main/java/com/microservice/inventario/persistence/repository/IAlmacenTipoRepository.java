package com.microservice.inventario.persistence.repository;

import com.microservice.inventario.persistence.entity.AlmacenTipoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IAlmacenTipoRepository extends JpaRepository<AlmacenTipoEntity, Long> {
}
