package com.camal.microservice_finanzas.persistence.repository;

import com.camal.microservice_finanzas.persistence.entity.MonedasEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IMonedasRepository extends JpaRepository<MonedasEntity, String> {
    @Query("SELECT m FROM MonedasEntity m WHERE m.idEmpresa = :idEmpresa")
    List<MonedasEntity> findByIdEmpresa(Long idEmpresa);
}
