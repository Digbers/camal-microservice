package com.microservice.empresas.persistence.repository;

import com.microservice.empresas.persistence.entity.EntidadEntity;
import org.springframework.data.repository.CrudRepository;

public interface IEntidadRepository extends CrudRepository<EntidadEntity, Long> {
}
