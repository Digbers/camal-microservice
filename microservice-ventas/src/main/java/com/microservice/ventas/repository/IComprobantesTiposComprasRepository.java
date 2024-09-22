package com.microservice.ventas.repository;

import com.microservice.ventas.entity.ComprobantesTiposComprasEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IComprobantesTiposComprasRepository extends JpaRepository<ComprobantesTiposComprasEntity, String> {
}
