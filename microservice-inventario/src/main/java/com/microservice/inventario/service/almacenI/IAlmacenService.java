package com.microservice.inventario.service.almacenI;

import com.microservice.inventario.controller.DTO.AlmacenDTO;
import com.microservice.inventario.persistence.entity.AlmacenEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IAlmacenService {
    Page<AlmacenDTO> findAll(Pageable pageable);
    Optional<AlmacenDTO> findById(Long id);
    Optional<AlmacenEntity> findByIdAlmacenEntity(Long id);
    AlmacenDTO save(AlmacenDTO almacenDTO);
    boolean deleteById(Long id);
    List<AlmacenDTO> findByIdEmpresa(Long id);
}