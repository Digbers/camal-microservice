package com.microservice.empresas.persistence.repository;

import com.microservice.empresas.persistence.entity.EmpresaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface IEmpresaRepository extends JpaRepository<EmpresaEntity, Long>, JpaSpecificationExecutor<EmpresaEntity> {
    List<EmpresaEntity> findAllByIdIn(Set<Long> empresaIds);

}
