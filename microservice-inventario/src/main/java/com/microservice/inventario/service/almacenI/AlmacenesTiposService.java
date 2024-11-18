package com.microservice.inventario.service.almacenI;

import com.microservice.inventario.controller.DTO.AlmacenDTO;
import com.microservice.inventario.controller.DTO.AlmacenTipoDTO;
import com.microservice.inventario.persistence.entity.AlmacenTipoEntity;
import com.microservice.inventario.persistence.repository.IAlmacenTipoRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class AlmacenesTiposService implements IAlmacenesTiposService {
    private final IAlmacenTipoRepository iAlmacenTipoRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<AlmacenTipoDTO> findByIdEmpresa(Long idEmpresa) {
        try {
            List<AlmacenTipoEntity> almacenTipo = iAlmacenTipoRepository.findAllByIdEmpresa(idEmpresa);
            return almacenTipo.stream().map(almacenTipoEntity -> modelMapper.map(almacenTipoEntity, AlmacenTipoDTO.class)).toList();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error al obtener almacenes");
        }
    }
}
