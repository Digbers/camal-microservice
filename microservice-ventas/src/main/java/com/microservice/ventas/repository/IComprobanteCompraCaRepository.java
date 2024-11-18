package com.microservice.ventas.repository;

import com.microservice.ventas.entity.ComprobantesComprasCaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IComprobanteCompraCaRepository extends JpaRepository<ComprobantesComprasCaEntity, Long>, JpaSpecificationExecutor<ComprobantesComprasCaEntity> {
}
