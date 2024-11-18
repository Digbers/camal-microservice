package com.microservice.ventas.repository;

import com.microservice.ventas.entity.ComprobantesTiposVentasEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface IComprobantesTiposVentasRepository extends JpaRepository<ComprobantesTiposVentasEntity, Long>, JpaSpecificationExecutor<ComprobantesTiposVentasEntity> {
    List<ComprobantesTiposVentasEntity> findAllByIdEmpresa(Long idEmpresa);
    Optional<ComprobantesTiposVentasEntity> findByIdEmpresaAndCodigo(Long idEmpresa, String codigo);
}
