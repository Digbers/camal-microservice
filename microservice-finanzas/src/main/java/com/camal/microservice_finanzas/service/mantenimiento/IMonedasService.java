package com.camal.microservice_finanzas.service.mantenimiento;

import com.camal.microservice_finanzas.controller.DTO.MonedasDTO;

import java.util.List;

public interface IMonedasService {
    MonedasDTO save(MonedasDTO monedasDTO);
    boolean deleteById(String id);
    MonedasDTO update(String id, MonedasDTO monedasDTO);
    MonedasDTO findById(String id);
    List<MonedasDTO> findByIdEmpresa(Long idEmpresa);
    List<MonedasDTO> findAll();
}
