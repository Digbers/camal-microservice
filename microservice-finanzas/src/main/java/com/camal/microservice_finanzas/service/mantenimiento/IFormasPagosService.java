package com.camal.microservice_finanzas.service.mantenimiento;

import com.camal.microservice_finanzas.controller.DTO.FormasPagosDTO;

import java.util.List;

public interface IFormasPagosService {
    FormasPagosDTO save(FormasPagosDTO formasPagosDTO);
    FormasPagosDTO findById(Long id);
    boolean deleteById(Long id);
    FormasPagosDTO update(Long id, FormasPagosDTO formasPagosDTO);
    List<FormasPagosDTO> findByIdEmpresa(Long idEmpresa);
    List<FormasPagosDTO> findAll();
}
