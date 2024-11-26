package com.microservice.inventario.service.unidades;

import com.microservice.inventario.controller.DTO.UnidadesDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IUnidadesService {
    List<UnidadesDTO> findByIdEmpresa(Long idEmpresa);
    UnidadesDTO save(UnidadesDTO unidadesDTO);
    UnidadesDTO update(Long id, UnidadesDTO unidadesDTO);
    void deleteById(Long id);
    Page<UnidadesDTO> findAllByEmpresa(String codigo, String nombre, Pageable pageable, Long idEmpresa);
}
