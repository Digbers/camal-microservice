package com.microservice.empresas.service.impl;

import com.microservice.empresas.controller.dto.EntidadDTO;
import com.microservice.empresas.persistence.entity.EntidadEntity;
import com.microservice.empresas.persistence.repository.IEntidadRepository;
import com.microservice.empresas.service.IEntidadService;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class EntidadServiceImpl implements IEntidadService {
    @Autowired
    private IEntidadRepository entidadRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Override
    public List<EntidadEntity> findAll() {
        return (List<EntidadEntity>) entidadRepository.findAll();
    }

    @Override
    public Optional<EntidadEntity> findById(Long id) {
        return entidadRepository.findById(id);
    }

    @Override
    public EntidadEntity save(EntidadDTO entidadDTO) {
        EntidadEntity entidadEntity = modelMapper.map(entidadDTO, EntidadEntity.class);
        return entidadRepository.save(entidadEntity);
    }

    @Override
    public void deleteById(Long id) {
        entidadRepository.deleteById(id);
    }

    @Override
    public EntidadEntity update(Long id, EntidadDTO entidadDTO) {
        EntidadEntity entidad = entidadRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entidad no encontrada con id: " + id));

        entidad.setEmpresa(entidadDTO.getEmpresa());
        entidad.setNombre(entidadDTO.getNombre());
        entidad.setCelular(entidadDTO.getCelular());
        entidad.setDireccion(entidadDTO.getDireccion());
        entidad.setApellidoMaterno(entidadDTO.getApellidoMaterno());
        entidad.setApellidoPaterno(entidadDTO.getApellidoPaterno());
        entidad.setEmail(entidadDTO.getEmail());
        entidad.setEstado(entidadDTO.getEstado());
        entidad.setDocumentoTipo(entidadDTO.getDocumentoTipo());
        entidad.setNroDocumento(entidadDTO.getNroDocumento());
        entidad.setUsuarioActualizacion(entidadDTO.getUsuarioActualizacion());
        entidad.setSexo(entidadDTO.getSexo());
        entidad.setZona(entidadDTO.getZona());
        entidad.getEntidadesTiposList().clear();
        if (entidadDTO.getEntidadesTiposList() != null) {
            entidad.getEntidadesTiposList().addAll(entidadDTO.getEntidadesTiposList());
        }
        return entidadRepository.save(entidad);
    }
}
