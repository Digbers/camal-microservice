package com.microservice.inventario.service.envases;

import com.microservice.inventario.controller.DTO.EnvaseDTO;
import com.microservice.inventario.exception.EnvaseNotFoundException;
import com.microservice.inventario.persistence.entity.EnvaseEntity;
import com.microservice.inventario.persistence.especification.EnvaseSpecifications;
import com.microservice.inventario.persistence.repository.EnvaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EnvaseService implements IEnvaseService{
    private final EnvaseRepository envaseRepository;
    public final ModelMapper modelMapper;

    @Override
    public EnvaseDTO save(EnvaseDTO envaseDTO) {
        try {
            EnvaseEntity envase = modelMapper.map(envaseDTO, EnvaseEntity.class);
            envaseRepository.save(envase);
            return modelMapper.map(envase, EnvaseDTO.class);
        } catch (Exception e) {
            throw new EnvaseNotFoundException("No se pudo guardar el envase");
        }
    }

    @Override
    public Page<EnvaseDTO> findAll(Long idEnvase, Long idEmpresa, String tipoEnvase,String descripcion, Integer capacidad, Double pesoReferencia, String estado,Pageable pageable) {
        log.info("Buscando envase con parametros: idEnvase: {}, idEmpresa: {}, tipoEnvase: {}, descripcion: {}, capacidad: {}, pesoReferencia: {}, estado: {}", idEnvase, idEmpresa, tipoEnvase, descripcion, capacidad, pesoReferencia, estado);
        try {
            Specification<EnvaseEntity> specification = EnvaseSpecifications.getEnvase(idEnvase, idEmpresa, tipoEnvase, descripcion, capacidad, pesoReferencia, estado);
            return envaseRepository.findAll(specification, pageable).map(envase -> {
                EnvaseDTO dtoEnvase = modelMapper.map(envase, EnvaseDTO.class);
                return dtoEnvase;
            });
        } catch (Exception e) {
            throw new EnvaseNotFoundException("No se pudo listar los envases");
        }
    }

    @Override
    public List<EnvaseDTO> findByIdAlmacen(Long id) {
        try {
            List<EnvaseEntity> envaseList = envaseRepository.findEnvasesByAlmacen(id);
            return envaseList.stream()
                    .map(envase -> {
                        EnvaseDTO dtoEnvase = modelMapper.map(envase, EnvaseDTO.class);
                        return dtoEnvase;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new EnvaseNotFoundException("No se pudo listar los envases");
        }
    }
}
