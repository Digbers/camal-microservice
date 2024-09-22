package com.microservice.ventas.repository;

import com.microservice.ventas.entity.ComprobantesComprasEstadosEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IComprobantesCompraEstadoRepository extends JpaRepository<ComprobantesComprasEstadosEntity, String> {
}
