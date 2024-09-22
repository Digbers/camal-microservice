package com.microservice.inventario.service.puntoVentaI;

import com.microservice.inventario.controller.DTO.AlmacenDTO;
import com.microservice.inventario.controller.DTO.PuntoVentaDTO;
import com.microservice.inventario.persistence.entity.AlmacenEntity;
import com.microservice.inventario.persistence.entity.PuntoVentaEntity;
import com.microservice.inventario.persistence.repository.IPuntoVentaRepository;
import com.microservice.inventario.service.almacenI.IAlmacenService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PuntoVentaService implements IPuntoVentaService {
    @Autowired
    IPuntoVentaRepository iPuntoVentaRepository;
    @Autowired
    IAlmacenService iAlmacenService;
    @Autowired
    ModelMapper modelMapper;
    @Override
    public Page<PuntoVentaDTO> findAll(Pageable pageable) {
        return iPuntoVentaRepository.findAll(pageable).map(puntoVenta -> modelMapper.map(puntoVenta, PuntoVentaDTO.class));
    }

    @Override
    public Optional<PuntoVentaDTO> findById(Long id) {
        return iPuntoVentaRepository.findById(id).map(puntoVenta -> modelMapper.map(puntoVenta, PuntoVentaDTO.class));
    }

    @Override
    public PuntoVentaDTO save(PuntoVentaDTO puntoVentaDTO) {
        PuntoVentaEntity puntoVentaEntity = modelMapper.map(puntoVentaDTO, PuntoVentaEntity.class);
        PuntoVentaEntity savedPuntoVentaEntity = iPuntoVentaRepository.save(puntoVentaEntity);
        return modelMapper.map(savedPuntoVentaEntity, PuntoVentaDTO.class);
    }

    @Override
    public boolean deleteById(Long id) {
        if (iPuntoVentaRepository.findById(id).isPresent()) {
            iPuntoVentaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public List<PuntoVentaDTO> findAllByIdAlmacen(Long id) {
        try {
            return iPuntoVentaRepository.findAllByIdAlmacen(id).stream().map(puntoVenta -> modelMapper.map(puntoVenta, PuntoVentaDTO.class)).toList();
        } catch (Exception e) {
            return List.of();
        }
    }
}
