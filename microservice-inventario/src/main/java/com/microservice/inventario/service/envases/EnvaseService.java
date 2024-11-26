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
    public EnvaseDTO update(Long id, EnvaseDTO envaseDTO) {
        try {
            EnvaseEntity envase = envaseRepository.findById(id).orElseThrow(() -> new EnvaseNotFoundException("No se encontro el envase con id: " + id));
            EnvaseEntity envaseUpdated = modelMapper.map(envaseDTO, EnvaseEntity.class);
            envase.setIdEmpresa(envaseDTO.getIdEmpresa());
            envase.setTipoEnvase(envaseDTO.getTipoEnvase());
            envase.setDescripcion(envaseDTO.getDescripcion());
            envase.setCapacidad(envaseDTO.getCapacidad());
            envase.setPesoReferencia(envaseDTO.getPesoReferencia());
            envase.setEstado(envaseDTO.getEstado());
            envase.setUsuarioActualizacion(envaseDTO.getUsuarioActualizacion());
            envaseRepository.save(envase);
            return modelMapper.map(envase, EnvaseDTO.class);
        } catch (Exception e) {
            throw new EnvaseNotFoundException("No se pudo actualizar el envase");
        }
    }

    @Override
    public Page<EnvaseDTO> findAll(Long idEnvase, String tipoEnvase,String descripcion, Integer capacidad, Double pesoReferencia, String estado,Pageable pageable, Long idEmpresa) {
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

    @Override
    public List<EnvaseDTO> findByIdEmpresa(Long idEmpresa) {
        try{
            List<EnvaseEntity> envaseList = envaseRepository.findByIdEmpresa(idEmpresa);
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
