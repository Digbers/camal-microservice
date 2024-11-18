package com.microservice.ventas.repository;

import com.microservice.ventas.entity.ComprobantesVentasEstadoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IComprobantesVentasEstadosRepository extends JpaRepository<ComprobantesVentasEstadoEntity, Long>, JpaSpecificationExecutor<ComprobantesVentasEstadoEntity> {
    List<ComprobantesVentasEstadoEntity> findByIdEmpresa(Long idEmpresa);
    Optional<ComprobantesVentasEstadoEntity> findByIdEmpresaAndCodigo(Long idEmpresa, String codigo);
}
