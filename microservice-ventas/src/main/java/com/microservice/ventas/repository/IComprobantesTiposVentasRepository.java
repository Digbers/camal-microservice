package com.microservice.ventas.repository;

import com.microservice.ventas.entity.ComprobantesTiposVentasEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IComprobantesTiposVentasRepository extends JpaRepository<ComprobantesTiposVentasEntity, String> {
    List<ComprobantesTiposVentasEntity> findAllByIdEmpresa(Long idEmpresa);
}
