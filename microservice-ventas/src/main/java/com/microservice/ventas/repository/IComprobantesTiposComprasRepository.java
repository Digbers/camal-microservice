package com.microservice.ventas.repository;

import com.microservice.ventas.entity.ComprobantesTiposComprasEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface IComprobantesTiposComprasRepository extends JpaRepository<ComprobantesTiposComprasEntity, Long>, JpaSpecificationExecutor<ComprobantesTiposComprasEntity> {
    List<ComprobantesTiposComprasEntity> findByIdEmpresa(Long idEmpresa);
    Optional<ComprobantesTiposComprasEntity> findByIdEmpresaAndCodigo(Long idEmpresa, String codigo);
}
