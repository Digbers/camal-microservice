package com.microservice.ventas.repository;

import com.microservice.ventas.controller.DTO.compras.ComprobantesComprasEstadosDTO;
import com.microservice.ventas.entity.ComprobantesComprasEstadosEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IComprobantesCompraEstadoRepository extends JpaRepository<ComprobantesComprasEstadosEntity, Long>, JpaSpecificationExecutor<ComprobantesComprasEstadosEntity> {
    List<ComprobantesComprasEstadosEntity> findByIdEmpresa(Long idEmpresa);
    Optional<ComprobantesComprasEstadosEntity> findByIdEmpresaAndCodigo(Long idEmpresa, String codigo);
}
