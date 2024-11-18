package com.microservice.empresas.service;

import com.microservice.empresas.controller.dto.ZonasDTO;
import com.microservice.empresas.persistence.entity.ZonasEntity;

import java.util.List;
import java.util.Optional;

public interface IZonasService {
    List<ZonasDTO> findAll();

    Optional<ZonasDTO> findById(Long id);

    ZonasDTO save(ZonasDTO zonaDTO);

    void deleteById(Long id);
}
