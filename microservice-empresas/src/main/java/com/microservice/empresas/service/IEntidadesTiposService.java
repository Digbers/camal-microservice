package com.microservice.empresas.service;

import com.microservice.empresas.controller.dto.EntidadesTiposDTO;
import com.microservice.empresas.persistence.entity.EntidadesTiposEntity;

import java.util.List;

public interface IEntidadesTiposService {
    List<EntidadesTiposEntity> findAll();

    EntidadesTiposEntity findById(String tipoCodigo,Long empresa);

    EntidadesTiposEntity save(EntidadesTiposDTO entidadesTiposDTO);

    void deleteById(String tipoCodigo,Long empresa);

    EntidadesTiposEntity update(String tipoCodigo,Long empresa,EntidadesTiposDTO entidadesTiposDTO);
}
