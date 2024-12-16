package com.camal.microservice_finanzas.service.mantenimiento;

import com.camal.microservice_finanzas.controller.DTO.MonedasDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IMonedasService {
    MonedasDTO save(MonedasDTO monedasDTO);
    MonedasDTO findByEmpresaAndCodigo(Long idEmpresa, String codigo);
    boolean deleteById(Long id);
    MonedasDTO update(Long id, MonedasDTO monedasDTO);
    MonedasDTO findById(Long id);
    List<MonedasDTO> findByIdEmpresa(Long idEmpresa);
    List<MonedasDTO> findAll();
    Page<MonedasDTO> findAll(String codigo, String nombre,String simbolo, Pageable pageable, Long idEmpresa);

}
