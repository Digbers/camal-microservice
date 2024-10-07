package com.camal.microservice_finanzas.service.mantenimiento;

import com.camal.microservice_finanzas.controller.DTO.FormasCobrosDTO;
import com.camal.microservice_finanzas.controller.DTO.FormasDeCobrosDTO;

import java.util.List;

public interface IFormasCobrosService {
    FormasDeCobrosDTO save(FormasDeCobrosDTO formasCobrosDTO);
    FormasDeCobrosDTO findById(String id);
    boolean deleteById(String id);
    FormasDeCobrosDTO update(String id, FormasDeCobrosDTO formasCobrosDTO);
    List<FormasDeCobrosDTO> findByIdEmpresa(Long idEmpresa);
    List<FormasDeCobrosDTO> findAll();

}
