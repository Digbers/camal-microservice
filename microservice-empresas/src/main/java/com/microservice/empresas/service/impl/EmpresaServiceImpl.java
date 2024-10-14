package com.microservice.empresas.service.impl;

import com.microservice.empresas.controller.dto.EmpresaDTO;
import com.microservice.empresas.persistence.entity.EmpresaEntity;
import com.microservice.empresas.persistence.repository.IEmpresaRepository;
import com.microservice.empresas.response.EmpresaResponseDTO;
import com.microservice.empresas.service.IEmpresaService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmpresaServiceImpl implements IEmpresaService {

    private final IEmpresaRepository empresaRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<EmpresaEntity> findAll() {
        return (List<EmpresaEntity>) empresaRepository.findAll();
    }

    @Override
    public Optional<EmpresaEntity> findById(Long id) {
        return empresaRepository.findById(id);
    }

    @Override
    public EmpresaEntity save(EmpresaDTO empresaDTO) {
        EmpresaEntity empresaEntity = modelMapper.map(empresaDTO, EmpresaEntity.class);
        return empresaRepository.save(empresaEntity);
    }

    @Override
    public void deleteById(Long id) {
        empresaRepository.deleteById(id);
    }

    @Override
    public EmpresaEntity update(Long id, EmpresaDTO empresaDTO) {
        EmpresaEntity empresa = empresaRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Empresa no encontrada con id: " + id));

        empresa.setEmpresaCodigo(empresaDTO.getEmpresaCodigo());
        empresa.setDistrito(empresaDTO.getDistrito());
        empresa.setDepartamento(empresaDTO.getDepartamento());
        empresa.setProvincia(empresaDTO.getProvincia());
        empresa.setCelular(empresaDTO.getCelular());
        empresa.setCorreo(empresaDTO.getCorreo());
        empresa.setDireccion(empresaDTO.getDireccion());
        empresa.setEstado(empresaDTO.getEstado());
        empresa.setLogo(empresaDTO.getLogo());
        empresa.setRazonSocial(empresaDTO.getRazonSocial());
        empresa.setRuc(empresaDTO.getRuc());
        empresa.setTelefono(empresaDTO.getTelefono());
        empresa.setUbigeo(empresaDTO.getUbigeo());
        empresa.setUsuarioActualizacion(empresaDTO.getUsuarioActualizacion());
        empresa.setWeb(empresaDTO.getWeb());

        return empresaRepository.save(empresa);
    }

    @Override
    public List<EmpresaResponseDTO> findAllByIds(Set<Long> empresaIds) {
        List<EmpresaEntity> empresas = empresaRepository.findAllById(empresaIds);
        return empresas.stream()
                .map(empresa -> modelMapper.map(empresa, EmpresaResponseDTO.class))
                .collect(Collectors.toList());
    }
}
