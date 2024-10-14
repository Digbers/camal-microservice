package com.microservice.empresas.persistence.repository;

import com.microservice.empresas.persistence.entity.EmpresaEntity;
import com.microservice.empresas.persistence.entity.EntidadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IEntidadRepository extends JpaRepository<EntidadEntity, Long> {
    Optional<EntidadEntity> findByNroDocumento(String nroDocumento);
    Optional<EntidadEntity> findByEmpresaAndNroDocumento(EmpresaEntity empresa, String nroDocumento);
}
