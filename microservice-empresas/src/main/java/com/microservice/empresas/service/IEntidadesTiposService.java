package com.microservice.empresas.service;

import com.microservice.empresas.controller.dto.EntidadesTiposDTO;
import com.microservice.empresas.persistence.entity.EntidadesTiposEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IEntidadesTiposService {
    Page<EntidadesTiposDTO> findAllByEmpresa(String tipoCodigo, String descripcion, Pageable pageable, Long idEmpresa);

    EntidadesTiposDTO findById(String tipoCodigo,Long empresa);

    EntidadesTiposDTO save(EntidadesTiposDTO entidadesTiposDTO);
    EntidadesTiposDTO update(Long id, EntidadesTiposDTO entidadesTiposDTO);
    void deleteByIdOriginal(Long id);

    void deleteById(String tipoCodigo,Long empresa);

    EntidadesTiposDTO update(String tipoCodigo,Long empresa,EntidadesTiposDTO entidadesTiposDTO);
}
