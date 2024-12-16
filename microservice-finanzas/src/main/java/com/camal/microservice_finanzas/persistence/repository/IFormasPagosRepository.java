package com.camal.microservice_finanzas.persistence.repository;

import com.camal.microservice_finanzas.persistence.entity.FormasPagosEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IFormasPagosRepository extends JpaRepository<FormasPagosEntity, Long> {
    List<FormasPagosEntity> findByIdEmpresa(Long idEmpresa);
    Optional<FormasPagosEntity> findByIdEmpresaAndCodigo(Long idEmpresa, String codigo);
}
