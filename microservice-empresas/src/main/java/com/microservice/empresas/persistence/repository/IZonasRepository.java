package com.microservice.empresas.persistence.repository;

import com.microservice.empresas.persistence.entity.ZonasEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface IZonasRepository extends JpaRepository<ZonasEntity, Long> {
}
