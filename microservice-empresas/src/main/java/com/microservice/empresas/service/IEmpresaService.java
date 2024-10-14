package com.microservice.empresas.service;

import com.microservice.empresas.controller.dto.EmpresaDTO;
import com.microservice.empresas.persistence.entity.EmpresaEntity;
import com.microservice.empresas.response.EmpresaResponseDTO;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface IEmpresaService {
    List<EmpresaEntity> findAll();

    Optional<EmpresaEntity> findById(Long id);

    EmpresaEntity save(EmpresaDTO empresaDTO);

    void deleteById(Long id);

    EmpresaEntity update(Long id,EmpresaDTO empresaDTO);
    List<EmpresaResponseDTO> findAllByIds(Set<Long> empresaIds);
}
