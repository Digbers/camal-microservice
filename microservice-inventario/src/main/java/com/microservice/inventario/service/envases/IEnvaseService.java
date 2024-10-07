package com.microservice.inventario.service.envases;

import com.microservice.inventario.controller.DTO.EnvaseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IEnvaseService {
    EnvaseDTO save(EnvaseDTO envaseDTO);
    Page<EnvaseDTO> findAll(Long idEnvase, Long idEmpresa, String tipoEnvase,String descripcion, Integer capacidad, Double pesoReferencia, String estado,Pageable pageable);
    List<EnvaseDTO> findByIdAlmacen(Long id);
}
