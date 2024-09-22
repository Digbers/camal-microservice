package com.microservice.empresas.service.impl;

import com.microservice.empresas.controller.dto.ZonasDTO;
import com.microservice.empresas.persistence.entity.ZonasEntity;
import com.microservice.empresas.persistence.repository.IZonasRepository;
import com.microservice.empresas.service.IZonasService;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class ZonasServiceImpl implements IZonasService {
    @Autowired
    private IZonasRepository zonaRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Override
    public List<ZonasEntity> findAll() {
        return (List<ZonasEntity>) zonaRepository.findAll();
    }

    @Override
    public Optional<ZonasEntity> findById(Long id) {
        return zonaRepository.findById(id);
    }

    @Override
    public ZonasEntity save(ZonasDTO zonaDTO) {
        ZonasEntity zonaEntity = modelMapper.map(zonaDTO, ZonasEntity.class);
        return zonaRepository.save(zonaEntity);
    }

    @Override
    public void deleteById(Long id) {
        zonaRepository.deleteById(id);
    }

    @Override
    public ZonasEntity update(Long id, ZonasDTO zonaDTO) {
        ZonasEntity zona = zonaRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Zona no encontrada con id: " + id));;
        zona.setNombre(zonaDTO.getNombre());
        zona.setDistrito(zonaDTO.getDistrito());
        zona.setDepartamento(zonaDTO.getDepartamento());
        zona.setProvincia(zonaDTO.getProvincia());
        zona.setUbiCodigo(zonaDTO.getUbiCodigo());
        zona.setEmpresa(zonaDTO.getEmpresa());
        zona.setUsuarioActualizacion(zonaDTO.getUsuarioActualizacion());
        return zonaRepository.save(zona);
    }
}
