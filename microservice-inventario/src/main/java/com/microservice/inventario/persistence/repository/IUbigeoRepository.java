package com.microservice.inventario.persistence.repository;

import com.microservice.inventario.persistence.entity.UbigeoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUbigeoRepository extends JpaRepository<UbigeoEntity, Long> {
}
