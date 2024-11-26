package com.microservice.inventario.service.productos;

import com.microservice.inventario.controller.DTO.ProductosTiposDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IProductosTiposService {
    Page<ProductosTiposDTO> findAllByEmpresa(String codigo,  String nombre, Pageable pageable, Long idEmpresa);
    ProductosTiposDTO save(ProductosTiposDTO productosTiposDTO);
    ProductosTiposDTO update(Long id, ProductosTiposDTO productosTiposDTO);
    void deleteById(Long id);
    List<ProductosTiposDTO> findByIdEmpresa(Long idEmpresa);
}
