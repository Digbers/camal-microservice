package com.microservice.inventario.service.almacenI;

import com.microservice.inventario.controller.DTO.AlmacenDTO;
import com.microservice.inventario.persistence.entity.AlmacenEntity;
import com.microservice.inventario.persistence.especification.AlmacenSpecifications;
import com.microservice.inventario.persistence.repository.IAlmacenRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AlmacenService implements IAlmacenService {

    private final IAlmacenRepository iAlmacenRepository;
    private final ModelMapper modelMapper;

    @Override
    public Page<AlmacenDTO> findAllByEmpresa(String nombre, String tipoAlmacen, Pageable pageable, Long idEmpresa) {
        try {
            Specification<AlmacenEntity> specification = AlmacenSpecifications.getAlmacenes(nombre, tipoAlmacen, idEmpresa);
            return iAlmacenRepository.findAll(specification, pageable).map(almacen -> modelMapper.map(almacen, AlmacenDTO.class));
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener almacenes por nombre y tipoAlmacen: " + e.getMessage());
        }
    }

    @Override
    public Optional<AlmacenDTO> findById(Long id) {
        return iAlmacenRepository.findById(id).map(almacen -> modelMapper.map(almacen, AlmacenDTO.class));
    }

    @Override
    public Optional<AlmacenEntity> findByIdAlmacenEntity(Long id) {
        if (iAlmacenRepository.findById(id).isPresent()) {
            return Optional.of(iAlmacenRepository.findById(id).get());
        }
        return Optional.empty();
    }

    @Override
    public AlmacenDTO save(AlmacenDTO almacenDTO) {
        AlmacenEntity almacenEntity = modelMapper.map(almacenDTO, AlmacenEntity.class);
        AlmacenEntity savedAlmacenEntity = iAlmacenRepository.save(almacenEntity);
        return modelMapper.map(savedAlmacenEntity, AlmacenDTO.class);
    }

    @Override
    public boolean deleteById(Long id) {
        if (iAlmacenRepository.findById(id).isPresent()) {
            iAlmacenRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public List<AlmacenDTO> findByIdEmpresa(Long id) {
        return iAlmacenRepository.findAllByIdEmpresa(id).stream().map(almacen -> modelMapper.map(almacen, AlmacenDTO.class)).toList();
    }
}
