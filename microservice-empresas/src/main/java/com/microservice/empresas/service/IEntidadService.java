package com.microservice.empresas.service;

import com.microservice.empresas.controller.dto.EntidadDTO;
import com.microservice.empresas.persistence.entity.EntidadEntity;
import com.microservice.empresas.persistence.entity.ZonasEntity;
import com.microservice.empresas.request.CreateEntidadRequest;
import com.microservice.empresas.response.EntidadResponse;
import com.microservice.empresas.response.IdsEntidades;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IEntidadService {
    Page<EntidadDTO> findAllByEmpresa(String nombre, String apellidoPaterno, String apellidoMaterno, String documentoTipo, String nroDocumento,String email,String celular, String direccion, String sexo, Boolean estado, String condicion, Pageable pageable, Long idEmpresa);

    Optional<EntidadDTO> findById(Long id);

    EntidadResponse save(CreateEntidadRequest entidadNew);

    void deleteById(Long id);
    IdsEntidades search(String nombre, String nroDocumento);
    List<EntidadResponse> findEntidadesByIds(List<Long> ids);
    List<EntidadResponse> autocompleteNroDocumento(String nroDocumento);
    List<EntidadResponse> autocompleteNombre(String nombre);
}
