package com.camal.microservice_finanzas.persistence.repository;

import com.camal.microservice_finanzas.persistence.entity.MonedasEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IMonedasRepository extends JpaRepository<MonedasEntity, Long>, JpaSpecificationExecutor<MonedasEntity> {
    @Query("SELECT m FROM MonedasEntity m WHERE m.idEmpresa = :idEmpresa")
    List<MonedasEntity> findByIdEmpresa(Long idEmpresa);
    Optional<MonedasEntity> findByIdEmpresaAndCodigo(Long idEmpresa, String codigo);
}
