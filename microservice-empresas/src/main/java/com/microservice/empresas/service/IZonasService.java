package com.microservice.empresas.service;

import com.microservice.empresas.controller.dto.ZonasDTO;
import com.microservice.empresas.persistence.entity.ZonasEntity;

import java.util.List;
import java.util.Optional;

public interface IZonasService {
    List<ZonasEntity> findAll();

    Optional<ZonasEntity> findById(Long id);

    ZonasEntity save(ZonasDTO zonaDTO);

    void deleteById(Long id);

    ZonasEntity update(Long id,ZonasDTO zonaDTO);
}
