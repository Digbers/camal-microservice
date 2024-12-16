package com.microservice.empresas.service;

import com.microservice.empresas.controller.dto.EntidadDTO;
import com.microservice.empresas.persistence.entity.EntidadEntity;
import com.microservice.empresas.persistence.entity.ZonasEntity;
import com.microservice.empresas.request.AsistenciaRequest;
import com.microservice.empresas.request.CreateEntidadRequest;
import com.microservice.empresas.response.EntidadResponse;
import com.microservice.empresas.response.EntidadResponseAsistencias;
import com.microservice.empresas.response.IdsEntidades;
import com.microservice.empresas.response.TrabajadoresResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IEntidadService {
    Page<EntidadDTO> findAllByEmpresa(String nombre, String apellidoPaterno, String apellidoMaterno, String documentoTipo, String nroDocumento,String email,String celular, String direccion, String sexo, Boolean estado, String condicion, Pageable pageable, Long idEmpresa);

    Optional<EntidadDTO> findById(Long id);

    EntidadResponse save(CreateEntidadRequest entidadNew);
    EntidadResponse update(Long id, CreateEntidadRequest entidad);

    void deleteById(Long id);
    IdsEntidades search(String nombre, String nroDocumento);
    List<EntidadResponse> findEntidadesByIds(List<Long> ids);
    List<EntidadResponse> autocompleteNroDocumento(String nroDocumento);
    List<EntidadResponse> autocompleteNombre(String nombre);
    Page<EntidadResponseAsistencias> findWorkers(Long idEmpresa,String tipo, LocalDate startDate, LocalDate endDate, Pageable pageable);
    Page<TrabajadoresResponse> findAllWorkers(Long idEmpresa, String tipo, Pageable pageable);
    void marcarAsistencia(AsistenciaRequest asistencia);
}
