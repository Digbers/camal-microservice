package com.microservice.inventario.service.almacenI;

import com.microservice.inventario.controller.DTO.AlmacenDTO;
import com.microservice.inventario.controller.DTO.AlmacenTipoDTO;

import java.util.List;

public interface IAlmacenesTiposService {
    List<AlmacenTipoDTO> findByIdEmpresa(Long idEmpresa);
}
