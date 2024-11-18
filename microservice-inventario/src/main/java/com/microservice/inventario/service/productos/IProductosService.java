package com.microservice.inventario.service.productos;

import com.microservice.inventario.controller.DTO.ProductoDTO;
import com.microservice.inventario.controller.DTO.response.ProductoAResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface IProductosService {
    List<ProductoDTO> findByIdAlmacenProductoVenta(Long id);
    Boolean convertirProducto(Long idEmpresa, Long idAlmacen,Long idProducto, Long idEnvase, Integer cantidad, String codigoProductoConvert);
    Optional<ProductoDTO> findById(Long id);
    Page<ProductoDTO> finAll(Long id, Long idEmpresa, String codigo, String nombre, String tipoId, Long almacenId, Pageable pageable);
    Boolean deleteById(Long id);
    List<ProductoDTO> findAll();
    List<ProductoAResponse> findByDescripcionAutocomplete(String descripcion);
    ProductoDTO update(Long id, ProductoDTO productoRequest);
    ProductoDTO save(ProductoDTO productoRequest);
}
