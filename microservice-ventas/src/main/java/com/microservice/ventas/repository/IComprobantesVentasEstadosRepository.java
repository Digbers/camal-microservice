package com.microservice.ventas.repository;

import com.microservice.ventas.entity.ComprobantesVentasEstadoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IComprobantesVentasEstadosRepository extends JpaRepository<ComprobantesVentasEstadoEntity, String> {
}
