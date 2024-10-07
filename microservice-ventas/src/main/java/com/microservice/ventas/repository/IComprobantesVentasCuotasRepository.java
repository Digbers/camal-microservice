package com.microservice.ventas.repository;

import com.microservice.ventas.entity.ComprobantesVentasCuotasEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IComprobantesVentasCuotasRepository extends JpaRepository<ComprobantesVentasCuotasEntity, Long> {
}
