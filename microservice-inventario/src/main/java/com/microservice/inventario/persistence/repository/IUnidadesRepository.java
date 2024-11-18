package com.microservice.inventario.persistence.repository;

import com.microservice.inventario.persistence.entity.UnidadesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface IUnidadesRepository extends JpaRepository<UnidadesEntity, Long>, JpaSpecificationExecutor<UnidadesEntity> {
    Optional<UnidadesEntity> findByIdEmpresaAndCodigo(Long idEmpresa, String codigo);
    List<UnidadesEntity> findByIdEmpresa(Long idEmpresa);
}
