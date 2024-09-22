package com.microservice.empresas.persistence.repository;

import com.microservice.empresas.persistence.entity.EmpresaEntity;
import org.springframework.data.repository.CrudRepository;

public interface IEmpresaRepository extends CrudRepository<EmpresaEntity, Long> {
}
