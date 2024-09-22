package com.camal.microservice_finanzas.persistence.repository;

import com.camal.microservice_finanzas.persistence.entity.ComprobantesVentasCobrosEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IComprobantesVentasCobrosRepository extends JpaRepository<ComprobantesVentasCobrosEntity, Long> {
}
