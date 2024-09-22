package com.microservice.empresas.service;

import com.microservice.empresas.controller.dto.EntidadDTO;
import com.microservice.empresas.persistence.entity.EntidadEntity;
import com.microservice.empresas.persistence.entity.ZonasEntity;

import java.util.List;
import java.util.Optional;

public interface IEntidadService {
    List<EntidadEntity> findAll();

    Optional<EntidadEntity> findById(Long id);

    EntidadEntity save(EntidadDTO entidadDTO);

    void deleteById(Long id);

    EntidadEntity update(Long id,EntidadDTO entidadDTO);
}
