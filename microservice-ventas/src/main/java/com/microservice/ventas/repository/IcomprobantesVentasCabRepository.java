package com.microservice.ventas.repository;

import com.microservice.ventas.entity.ComprobantesVentasCabEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IcomprobantesVentasCabRepository extends JpaRepository<ComprobantesVentasCabEntity, Long> {
    @Query("SELECT MAX(c.numero) FROM ComprobantesVentasCabEntity c WHERE c.serie = ?1 AND c.idPuntoVenta = ?2")
    String findMaxNumero(String serie, Long idPuntoVenta);
}
