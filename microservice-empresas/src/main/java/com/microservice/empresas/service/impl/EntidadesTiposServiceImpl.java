package com.microservice.empresas.service.impl;

import com.microservice.empresas.controller.dto.EntidadesTiposDTO;
import com.microservice.empresas.persistence.entity.EntidadesTiposEntity;
import com.microservice.empresas.persistence.especification.EntidadesTiposEspecification;
import com.microservice.empresas.persistence.repository.IEntidadesTiposRepository;
import com.microservice.empresas.service.IEntidadesTiposService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EntidadesTiposServiceImpl implements IEntidadesTiposService {

    private final IEntidadesTiposRepository entidadesTiposRepository;
    private final ModelMapper modelMapper;

    @Override
    public Page<EntidadesTiposDTO> findAllByEmpresa(String tipoCodigo, String descripcion, Pageable pageable, Long idEmpresa) {
        try {
            Specification<EntidadesTiposEntity> specification = EntidadesTiposEspecification.getEntidadesTipos(tipoCodigo, descripcion, idEmpresa);
            return entidadesTiposRepository.findAll(specification, pageable).map(entidadesTipos -> modelMapper.map(entidadesTipos, EntidadesTiposDTO.class));
        } catch (Exception e) {
            log.error("Error al obtener EntidadesTipos", e);
            throw new RuntimeException("Error al obtener EntidadesTipos");
        }
    }

    @Override
    public EntidadesTiposDTO findById(String tipoCodigo, Long empresa) {
        try {
            EntidadesTiposEntity entidadesTipos = entidadesTiposRepository.findByEmpresaAndTipoCodigo(empresa, tipoCodigo).orElseThrow(() -> new EntityNotFoundException("EntidadTipos no encontrada con tipoCodigo: " + tipoCodigo + " y empresaId: " + empresa));
            return modelMapper.map(entidadesTipos, EntidadesTiposDTO.class);
        } catch (NullPointerException ex) {
            log.error("Error al obtener EntidadesTipos", ex);
            throw new RuntimeException("Error al obtener EntidadesTipos");
        }
    }

    @Override
    public EntidadesTiposDTO save(EntidadesTiposDTO entidadesTiposDTO) {
        try {
            EntidadesTiposEntity entidadesTiposEntity = modelMapper.map(entidadesTiposDTO, EntidadesTiposEntity.class);
            EntidadesTiposEntity entidadesTipos = entidadesTiposRepository.save(entidadesTiposEntity);
            return modelMapper.map(entidadesTipos, EntidadesTiposDTO.class);
        } catch (Exception e) {
            log.error("Error al guardar EntidadesTipos", e);
            throw new RuntimeException("Error al guardar EntidadesTipos");
        }
    }

    @Override
    public void deleteById(String tipoCodigo,Long empresa) {
        try {
            EntidadesTiposEntity entidadesTipos = entidadesTiposRepository.findByEmpresaAndTipoCodigo(empresa, tipoCodigo).orElseThrow(() -> new EntityNotFoundException("EntidadTipos no encontrada con tipoCodigo: " + tipoCodigo + " y empresaId: " + empresa));
            entidadesTiposRepository.delete(entidadesTipos);
        } catch (Exception e) {
            log.error("Error al eliminar EntidadesTipos", e);
            throw new RuntimeException("Error al eliminar EntidadesTipos");
        }
    }

    @Override
    public EntidadesTiposDTO update(String tipoCodigo, Long empresa, EntidadesTiposDTO entidadesTiposDTO) {
        try {
            EntidadesTiposEntity entidadExistente = entidadesTiposRepository.findByEmpresaAndTipoCodigo(empresa, tipoCodigo)
                    .orElseThrow(() -> new EntityNotFoundException("EntidadTipos no encontrada con tipoCodigo: " + tipoCodigo + " y empresaId: " + empresa));

            entidadExistente.setTipoCodigo(entidadesTiposDTO.getTipoCodigo());
            entidadExistente.setDescripcion(entidadesTiposDTO.getDescripcion());
            entidadExistente.setUsuarioActualizacion(entidadesTiposDTO.getUsuarioActualizacion());

            EntidadesTiposEntity entidadesTipos = entidadesTiposRepository.save(entidadExistente);
            return modelMapper.map(entidadesTipos, EntidadesTiposDTO.class);
        } catch (Exception e) {
            log.error("Error al actualizar EntidadesTipos", e);
            throw new RuntimeException("Error al actualizar EntidadesTipos");
        }
    }
}
