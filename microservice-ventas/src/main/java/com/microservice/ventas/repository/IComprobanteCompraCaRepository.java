package com.microservice.ventas.repository;

import com.microservice.ventas.entity.ComprobantesComprasCaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IComprobanteCompraCaRepository extends JpaRepository<ComprobantesComprasCaEntity, Long> {
}
