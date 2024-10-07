package com.microservice.inventario.persistence.repository;

import com.microservice.inventario.persistence.entity.UnidadesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUnidadesRepository extends JpaRepository<UnidadesEntity, String> {
}
