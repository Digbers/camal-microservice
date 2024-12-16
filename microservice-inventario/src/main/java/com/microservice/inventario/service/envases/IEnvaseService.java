package com.microservice.inventario.service.envases;

import com.microservice.inventario.controller.DTO.EnvaseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IEnvaseService {
    EnvaseDTO save(EnvaseDTO envaseDTO);
    EnvaseDTO update(Long id, EnvaseDTO envaseDTO);
    Page<EnvaseDTO> findAll(Long idEnvase,String tipoEnvase,String descripcion, Integer capacidad, Double pesoReferencia, Boolean estado,Pageable pageable, Long idEmpresa);
    List<EnvaseDTO> findByIdAlmacen(Long id);
    List<EnvaseDTO> findByIdEmpresa(Long idEmpresa);

}
