package com.microservice.ventas.repository;

import com.microservice.ventas.entity.ComprobantesVentasCabEntity;
import com.microservice.ventas.entity.especification.ComprobantesVentasEspecification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IcomprobantesVentasCabRepository extends JpaRepository<ComprobantesVentasCabEntity, Long>, JpaSpecificationExecutor<ComprobantesVentasCabEntity> {
    @Query("SELECT MAX(c.numero) FROM ComprobantesVentasCabEntity c WHERE c.serie = ?1 AND c.idPuntoVenta = ?2")
    String findMaxNumero(String serie, Long idPuntoVenta);
}
