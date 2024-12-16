package com.microservice.empresas.service;

import com.microservice.empresas.controller.dto.EmpresaDTO;
import com.microservice.empresas.persistence.entity.EmpresaEntity;
import com.microservice.empresas.response.EmpresaResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface IEmpresaService {
    Page<EmpresaDTO> findAllByEmpresa(String razonSocial, String empresaCodigo, String ruc, String direccion, String departamento, String provincia, String distrito, String ubigeo, String telefono, String celular, String correo, String web, String logo, Boolean estado, Pageable pageable, Long idEmpresa);
    List<EmpresaEntity> findAll();


    Optional<EmpresaEntity> findById(Long id);

    EmpresaDTO save(EmpresaDTO empresaDTO, MultipartFile logo);

    void deleteById(Long id);

    EmpresaDTO update(Long id,EmpresaDTO empresaDTO, MultipartFile logo);
    List<EmpresaResponseDTO> findAllByIds(Set<Long> empresaIds);
}
