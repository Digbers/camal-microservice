package com.camal.microservice_finanzas.persistence.repository;

import com.camal.microservice_finanzas.persistence.entity.FormasCobrosEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IFormasCobrosRepository extends JpaRepository<FormasCobrosEntity, Long> {
    List<FormasCobrosEntity> findByIdEmpresa(Long idEmpresa);
    Optional<FormasCobrosEntity> findByIdEmpresaAndCodigo(Long idEmpresa, String codigo);
}
