package com.microservice.inventario.service.productos;

import com.microservice.inventario.controller.DTO.EnvaseDTO;
import com.microservice.inventario.controller.DTO.ProductoDTO;
import com.microservice.inventario.persistence.entity.ProductosEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

public interface IProductosService {
    List<ProductoDTO> findByIdAlmacenProductoVenta(Long id);
    ProductoDTO crearProductoYRegistrarStock(ProductoDTO producto);
    ProductoDTO guardarConversion(ProductoDTO productoDTO);
    Optional<ProductoDTO> findById(Long id);
    Page<ProductoDTO> finAll(Long id, Long idEmpresa, String codigo, String nombre, String tipoId, Long almacenId, Pageable pageable);
    Boolean deleteById(Long id);
    List<ProductoDTO> findAll();
    List<ProductoDTO> findByDescripcionAutocomplete(String descripcion);
    ProductoDTO update(Long id, ProductoDTO productoRequest);
    ProductoDTO save(ProductoDTO productoRequest);
}
