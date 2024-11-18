package com.microservice.ventas.repository;

import com.microservice.ventas.entity.ComprobantesComprasCuotasEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IComprobantesComprasCuotasRepository extends JpaRepository<ComprobantesComprasCuotasEntity, Long> {
}
