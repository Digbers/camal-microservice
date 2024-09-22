package com.microservice.empresas.service;

import com.microservice.empresas.controller.dto.EmpresaDTO;
import com.microservice.empresas.persistence.entity.EmpresaEntity;

import java.util.List;
import java.util.Optional;

public interface IEmpresaService {
    List<EmpresaEntity> findAll();

    Optional<EmpresaEntity> findById(Long id);

    EmpresaEntity save(EmpresaDTO empresaDTO);

    void deleteById(Long id);

    EmpresaEntity update(Long id,EmpresaDTO empresaDTO);
}
