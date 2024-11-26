package com.microservice.ventas.service.compras;

import com.microservice.ventas.controller.DTO.compras.ComprobantesComprasEstadosDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IComprasEstadosService {
    ComprobantesComprasEstadosDTO save(ComprobantesComprasEstadosDTO comprobantesComprasEstadosDTO);
    ComprobantesComprasEstadosDTO update(Long id, ComprobantesComprasEstadosDTO comprobantesComprasEstadosDTO);
    void deleteById(Long id);
    Page<ComprobantesComprasEstadosDTO> findAllByEmpresa(String codigo, String descripcion, Pageable pageable, Long idEmpresa);
    List<ComprobantesComprasEstadosDTO> findByIdEmpresa(Long idEmpresa);
}
