package com.camal.microservice_finanzas.persistence.repository;

import com.camal.microservice_finanzas.persistence.entity.FormasCobrosEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IFormasCobrosRepository extends JpaRepository<FormasCobrosEntity, String> {
}
