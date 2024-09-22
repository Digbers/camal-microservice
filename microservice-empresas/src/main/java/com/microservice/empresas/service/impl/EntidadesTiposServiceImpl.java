package com.microservice.empresas.service.impl;

import com.microservice.empresas.controller.dto.EntidadesTiposDTO;
import com.microservice.empresas.persistence.entity.EntidadesTiposEntity;
import com.microservice.empresas.persistence.entity.ids.EntidadesTiposId;
import com.microservice.empresas.persistence.repository.IEntidadesTiposRepository;
import com.microservice.empresas.service.IEntidadesTiposService;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class EntidadesTiposServiceImpl implements IEntidadesTiposService {

    @Autowired
    private IEntidadesTiposRepository entidadesTiposRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<EntidadesTiposEntity> findAll() {
        return (List<EntidadesTiposEntity>) entidadesTiposRepository.findAll();
    }

    @Override
    public EntidadesTiposEntity findById(String tipoCodigo, Long empresa) {
        EntidadesTiposId id = EntidadesTiposId.builder()
                .empresa(empresa)
                .tipoCodigo(tipoCodigo)
                .build();
        return entidadesTiposRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("EntidadTipos no encontrada con tipoCodigo: " + tipoCodigo + " y empresaId: " + empresa));
    }

    @Override
    public EntidadesTiposEntity save(EntidadesTiposDTO entidadesTiposDTO) {
        EntidadesTiposEntity entidadesTiposEntity = modelMapper.map(entidadesTiposDTO, EntidadesTiposEntity.class);
        return entidadesTiposRepository.save(entidadesTiposEntity);
    }

    @Override
    public void deleteById(String tipoCodigo,Long empresa) {
        EntidadesTiposId id = EntidadesTiposId.builder()
                .empresa(empresa)
                .tipoCodigo(tipoCodigo)
                .build();
        entidadesTiposRepository.deleteById(id);
    }

    @Override
    public EntidadesTiposEntity update(String tipoCodigo, Long empresa, EntidadesTiposDTO entidadesTiposDTO) {
        EntidadesTiposId id = EntidadesTiposId.builder()
                .empresa(empresa)
                .tipoCodigo(tipoCodigo)
                .build();
        EntidadesTiposEntity entidadExistente = entidadesTiposRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("EntidadTipos no encontrada con tipoCodigo: " + tipoCodigo + " y empresaId: " + empresa));

        entidadExistente.setTipoCodigo(entidadesTiposDTO.getTipoCodigo());
        entidadExistente.setDescripcion(entidadesTiposDTO.getDescripcion());
        entidadExistente.setUsuarioActualizacion(entidadesTiposDTO.getUsuarioActualizacion());

        return entidadesTiposRepository.save(entidadExistente);
    }
}
