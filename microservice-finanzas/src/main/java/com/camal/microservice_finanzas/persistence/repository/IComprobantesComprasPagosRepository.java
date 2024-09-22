package com.camal.microservice_finanzas.persistence.repository;

import com.camal.microservice_finanzas.persistence.entity.ComprobantesComprasPagosEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IComprobantesComprasPagosRepository extends JpaRepository<ComprobantesComprasPagosEntity, Long> {
}
