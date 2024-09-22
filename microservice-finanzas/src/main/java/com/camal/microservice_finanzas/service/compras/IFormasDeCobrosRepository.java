package com.camal.microservice_finanzas.service.compras;

import com.camal.microservice_finanzas.persistence.entity.FormasCobrosEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IFormasDeCobrosRepository extends JpaRepository<FormasCobrosEntity, String> {
}
