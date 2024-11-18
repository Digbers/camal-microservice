package com.microservice.empresas.persistence.repository;

import com.microservice.empresas.persistence.entity.EntidadesTiposEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface IEntidadesTiposRepository extends JpaRepository<EntidadesTiposEntity, Long>, JpaSpecificationExecutor<EntidadesTiposEntity> {
    @Query("SELECT e FROM EntidadesTiposEntity e WHERE e.empresa.id = :empresa AND e.tipoCodigo = :tipoCodigo")
    Optional<EntidadesTiposEntity> findByEmpresaAndTipoCodigo(Long empresa, String tipoCodigo);
    @Query("SELECT e FROM EntidadesTiposEntity e WHERE e.empresa.id = :empresa")
    List<EntidadesTiposEntity> findByEmpresa(Long empresa);
}
