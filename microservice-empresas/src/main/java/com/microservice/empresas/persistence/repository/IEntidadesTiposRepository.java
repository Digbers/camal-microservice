package com.microservice.empresas.persistence.repository;

import com.microservice.empresas.persistence.entity.EntidadesTiposEntity;
import com.microservice.empresas.persistence.entity.ids.EntidadesTiposId;
import org.springframework.data.repository.CrudRepository;

public interface IEntidadesTiposRepository extends CrudRepository<EntidadesTiposEntity, EntidadesTiposId> {
}
