package com.microservice.empresas.service.impl;

import com.microservice.empresas.controller.dto.ZonasDTO;
import com.microservice.empresas.persistence.entity.ZonasEntity;
import com.microservice.empresas.persistence.repository.IZonasRepository;
import com.microservice.empresas.service.IZonasService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ZonasServiceImpl implements IZonasService {
    private final IZonasRepository zonaRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<ZonasDTO> findAll() {
        List<ZonasEntity> zonasEntities = zonaRepository.findAll();
        return zonasEntities.stream().map(zona -> modelMapper.map(zona, ZonasDTO.class)).collect(Collectors.toList());
    }

    @Override
    public Optional<ZonasDTO> findById(Long id) {
        ZonasEntity zona = zonaRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Zona no encontrada con id: " + id));
        return Optional.of(modelMapper.map(zona, ZonasDTO.class));
    }

    @Override
    public ZonasDTO save(ZonasDTO zonaDTO) {
        ZonasEntity zonaEntity = modelMapper.map(zonaDTO, ZonasEntity.class);
        return modelMapper.map(zonaRepository.save(zonaEntity), ZonasDTO.class);
    }

    @Override
    public void deleteById(Long id) {
        zonaRepository.deleteById(id);
    }


}
