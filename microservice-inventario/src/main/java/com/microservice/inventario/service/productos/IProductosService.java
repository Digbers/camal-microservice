package com.microservice.inventario.service.productos;

import com.microservice.inventario.controller.DTO.ProductoDTO;
import com.microservice.inventario.persistence.entity.ProductosEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

public interface IProductosService {

    Page<ProductoDTO> finAll(Long id, Long idEmpresa, String codigo, String nombre, Long tipoId, Long almacenId, Pageable pageable);

    ProductoDTO save(ProductoDTO productoDTO);
    Boolean deleteById(Long id);
    ProductoDTO update(Long id, ProductoDTO productoDTO);
    Optional<ProductoDTO> findById(Long id);
    List<ProductoDTO> findAll();
    List<ProductoDTO> findStockMinimo();
    Integer obtenerStockDisponible(Long id);
}
