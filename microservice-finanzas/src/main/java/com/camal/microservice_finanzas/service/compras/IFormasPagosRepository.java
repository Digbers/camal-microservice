package com.camal.microservice_finanzas.service.compras;

import com.camal.microservice_finanzas.persistence.entity.FormasPagosEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IFormasPagosRepository extends JpaRepository<FormasPagosEntity, String> {
}
