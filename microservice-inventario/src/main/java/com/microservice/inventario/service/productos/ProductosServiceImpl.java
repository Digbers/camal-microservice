package com.microservice.inventario.service.productos;

import com.microservice.inventario.clients.EmpresaClient;
import com.microservice.inventario.controller.DTO.ComprobanteDetalleRequest;
import com.microservice.inventario.controller.DTO.ProductoDTO;
import com.microservice.inventario.controller.DTO.ProductosXAlmacenDTO;
import com.microservice.inventario.event.CompensarVentaEvent;
import com.microservice.inventario.event.VentaCreadaEvent;
import com.microservice.inventario.exception.ProductoNotFoundException;
import com.microservice.inventario.persistence.entity.AlmacenEntity;
import com.microservice.inventario.persistence.entity.ProductosEntity;
import com.microservice.inventario.persistence.entity.ProductosTiposEntity;
import com.microservice.inventario.persistence.entity.ProductosXAlmacenEntity;
import com.microservice.inventario.persistence.especification.ProductosSpecifications;
import com.microservice.inventario.persistence.repository.AlmacenRepository;
import com.microservice.inventario.persistence.repository.IProductosXAlmacenRepository;
import com.microservice.inventario.persistence.repository.ProductosRepository;
import com.microservice.inventario.persistence.repository.ProductosTiposRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductosServiceImpl implements IProductosService {
    @Autowired
    private ProductosRepository productosRepository;
    @Autowired
    private AlmacenRepository almacenRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private EmpresaClient empresaClient;
    @Autowired
    private ProductosTiposRepository tiposRepository;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private IProductosXAlmacenRepository productosXAlmacenRepository;

    @Override
    public Page<ProductoDTO> finAll(Long id, Long idEmpresa, String codigo, String nombre, Long tipoId, Long almacenId, Pageable pageable) {
        Specification<ProductosEntity> specification = ProductosSpecifications.getProductos(id, idEmpresa, codigo, nombre, tipoId, almacenId);

        return productosRepository.findAll(specification, pageable).map(producto -> {

            ProductoDTO dto = modelMapper.map(producto, ProductoDTO.class);
            List<ProductosXAlmacenDTO> productosXAlmacenDTOs = producto.getProductosXAlmacenes().stream()
                    .map(pa -> modelMapper.map(pa, ProductosXAlmacenDTO.class))
                    .collect(Collectors.toList());

            dto.setProductosXAlmacen(productosXAlmacenDTOs);
            return dto;
        });
    }
    @Override
    @Transactional
    public ProductoDTO save(ProductoDTO productoDTO) {
        boolean existeEmpresa = empresaClient.verificarEmpresaExiste(productoDTO.getEmpresaId());
        if (!existeEmpresa) {
            throw new RuntimeException("La empresa no existe");
        }
        Optional<ProductosEntity> existingProduct = productosRepository.findByCodigo(productoDTO.getCodigo());
        if (existingProduct.isPresent()) {
            throw new RuntimeException("El producto con el código especificado ya existe");
        }
        modelMapper.typeMap(ProductosXAlmacenDTO.class, ProductosXAlmacenEntity.class)
                .addMappings(mapper -> {
                    mapper.skip(ProductosXAlmacenEntity::setAlmacen);
                    mapper.skip(ProductosXAlmacenEntity::setProductos);
                });
        try {
            ProductosEntity producto = modelMapper.map(productoDTO, ProductosEntity.class);

            Optional<ProductosTiposEntity> tipoExistente = tiposRepository.findByCodigo(productoDTO.getTipo().getCodigo());
            if (tipoExistente.isPresent()) {
                producto.setTipo(tipoExistente.get());
            } else {
                ProductosTiposEntity nuevoTipo = modelMapper.map(productoDTO.getTipo(), ProductosTiposEntity.class);
                producto.setTipo(tiposRepository.save(nuevoTipo));
            }
            List<ProductosXAlmacenEntity> productosXAlmacenEntities = new ArrayList<>();
            for (ProductosXAlmacenDTO productosXAlmacenDTO : productoDTO.getProductosXAlmacen()) {
                ProductosXAlmacenEntity productosXAlmacenEntity = new ProductosXAlmacenEntity();
                AlmacenEntity almacen = almacenRepository.findById(productosXAlmacenDTO.getAlmacen())
                        .orElseThrow(() -> new EntityNotFoundException("Almacén no encontrado"));

                productosXAlmacenEntity.setAlmacen(almacen);
                productosXAlmacenEntity.setProductos(producto);
                productosXAlmacenEntity.setCantidad(productosXAlmacenDTO.getCantidad());
                productosXAlmacenEntity.setEmpresaId(producto.getEmpresaId());
                productosXAlmacenEntity.setUsuarioCreacion(producto.getUsuarioCreacion());
                productosXAlmacenEntities.add(productosXAlmacenEntity);
            }
            producto.setProductosXAlmacenes(productosXAlmacenEntities);

            ProductosEntity productoGuardado = productosRepository.save(producto);

            ProductoDTO dto = modelMapper.map(productoGuardado, ProductoDTO.class);
            List<ProductosXAlmacenDTO> productosXAlmacenDTOs = productoGuardado.getProductosXAlmacenes().stream()
                    .map(pa -> modelMapper.map(pa, ProductosXAlmacenDTO.class))
                    .collect(Collectors.toList());
            dto.setProductosXAlmacen(productosXAlmacenDTOs);

            return dto;
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar el producto", e);
        }
    }
    @Override
    @Transactional
    public Boolean deleteById(Long id) {
        Optional<ProductosEntity> producto = productosRepository.findById(id);
        if (producto.isPresent()) {
            productosRepository.delete(producto.get());
            return true;
        }
        return false;
    }
    @Override
    @Transactional
    public ProductoDTO update(Long id, ProductoDTO productoDTO) {
        // Verificar si la empresa existe de forma sincrónica
        boolean existeEmpresa = empresaClient.verificarEmpresaExiste(productoDTO.getEmpresaId());

        if (!existeEmpresa) {
            throw new RuntimeException("La empresa no existe");
        }
        // Obtener el producto con productosXAlmacenes cargado de forma sincrónica
        Optional<ProductosEntity> existingProductoOptional = productosRepository.findByIdWithAlmacenes(id);
        ProductosEntity existingProducto = existingProductoOptional
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));

        // Actualizar los atributos del producto
        existingProducto.setNombre(productoDTO.getNombre());
        existingProducto.setCodigo(productoDTO.getCodigo());
        existingProducto.setMarca(productoDTO.getMarca());
        existingProducto.setPresentacion(productoDTO.getPresentacion());
        existingProducto.setCapacidadCantidad(productoDTO.getCapacidadCantidad());
        existingProducto.setPrecioSugerido(productoDTO.getPrecioSugerido());
        existingProducto.setUsuarioActualizacion(productoDTO.getUsuActualizacion());
        existingProducto.setEmpresaId(productoDTO.getEmpresaId());
        existingProducto.setGenerarStock(productoDTO.getGenerarStock());
        existingProducto.setEstado(productoDTO.getEstado());

        // Verificar si el tipo de producto ya existe o se debe crear uno nuevo
        Optional<ProductosTiposEntity> tipoExistente = tiposRepository.findByCodigo(productoDTO.getTipo().getCodigo());
        if (tipoExistente.isPresent()) {
            existingProducto.setTipo(tipoExistente.get());
        } else {
            ProductosTiposEntity nuevoTipo = modelMapper.map(productoDTO.getTipo(), ProductosTiposEntity.class);
            existingProducto.setTipo(tiposRepository.save(nuevoTipo));
        }

        // Para actualizar las relaciones de ProductosXAlmacen
        List<ProductosXAlmacenEntity> productosXAlmacenEntities = new ArrayList<>();
        for (ProductosXAlmacenDTO productosXAlmacenDTO : productoDTO.getProductosXAlmacen()) {
            AlmacenEntity almacen = almacenRepository.findById(productosXAlmacenDTO.getAlmacen())
                    .orElseThrow(() -> new EntityNotFoundException("Almacén no encontrado"));

            // Verificar si ya existe una relación en ProductosXAlmacen para ese almacén
            ProductosXAlmacenEntity productosXAlmacenEntity = existingProducto.getProductosXAlmacenes().stream()
                    .filter(pxa -> pxa.getAlmacen().equals(almacen))
                    .findFirst()
                    .orElse(null);

            if (productosXAlmacenEntity == null) {
                productosXAlmacenEntity = new ProductosXAlmacenEntity();
                productosXAlmacenEntity.setAlmacen(almacen);
                productosXAlmacenEntity.setProductos(existingProducto);
                productosXAlmacenEntity.setUsuarioCreacion(existingProducto.getUsuarioCreacion());
            }

            productosXAlmacenEntity.setCantidad(productosXAlmacenDTO.getCantidad());
            productosXAlmacenEntity.setEmpresaId(existingProducto.getEmpresaId());

            productosXAlmacenEntities.add(productosXAlmacenEntity);
        }
        existingProducto.setProductosXAlmacenes(productosXAlmacenEntities);

        // Guardar el producto actualizado en la base de datos
        ProductosEntity updatedProducto = productosRepository.save(existingProducto);

        // Convertir la entidad actualizada en un DTO
        ProductoDTO dto = modelMapper.map(updatedProducto, ProductoDTO.class);
        List<ProductosXAlmacenDTO> productosXAlmacenDTOs = updatedProducto.getProductosXAlmacenes().stream()
                .map(pa -> modelMapper.map(pa, ProductosXAlmacenDTO.class))
                .collect(Collectors.toList());
        dto.setProductosXAlmacen(productosXAlmacenDTOs);

        return dto;
    }

    @Override
    public Optional<ProductoDTO> findById(Long id) {

        Optional<ProductosEntity> producto1 = productosRepository.findById(id);
        if (producto1.isEmpty()) {
            return Optional.empty();
        }
        ProductosEntity producto = producto1.get();
        ProductoDTO dto = modelMapper.map(producto, ProductoDTO.class);

        List<ProductosXAlmacenDTO> productosXAlmacenDTOs = producto.getProductosXAlmacenes().stream()
                .map(pa -> modelMapper.map(pa, ProductosXAlmacenDTO.class))
                .collect(Collectors.toList());
        dto.setProductosXAlmacen(productosXAlmacenDTOs);
        return Optional.of(dto);
    }

    @Override
    public List<ProductoDTO> findAll() {

        return productosRepository.findAll().stream()
                .peek(producto -> {
                    // Forzamos la carga de los productos por almacén
                    producto.getProductosXAlmacenes().forEach(pa -> {
                        pa.getAlmacen().getId();  // Cargar Almacen (LAZY)
                        pa.getProductos().getId(); // Cargar Producto (LAZY)
                    });
                })
                .map(producto -> {
                    ProductoDTO dto = modelMapper.map(producto, ProductoDTO.class);
                    // Mapear manualmente la lista de productos por almacén
                    List<ProductosXAlmacenDTO> productosXAlmacenDTOs = producto.getProductosXAlmacenes().stream()
                            .map(pa -> modelMapper.map(pa, ProductosXAlmacenDTO.class))
                            .collect(Collectors.toList());
                    dto.setProductosXAlmacen(productosXAlmacenDTOs);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<ProductoDTO> findStockMinimo() {
        System.out.println("Iniciando findStockMinimo");

        // Verificamos si se obtienen productos del repositorio
        List<ProductosEntity> productos = productosRepository.findStockMinimo();
        System.out.println("Productos obtenidos: " + productos.size());

        return productos.stream().map(producto -> {
            System.out.println("Mapeando producto con ID: " + producto.getId());

            ProductoDTO productoDTO = null;
            try {
                productoDTO = modelMapper.map(producto, ProductoDTO.class);
                System.out.println("Producto mapeado correctamente: " + productoDTO.getId());
            } catch (Exception e) {
                System.err.println("Error al mapear ProductoEntity a ProductoDTO: " + e.getMessage());
                e.printStackTrace();
            }

            // Verificamos si la lista de productosXAlmacenes está presente y no es nula
            if (producto.getProductosXAlmacenes() != null) {
                System.out.println("Productos por almacén obtenidos para producto ID " + producto.getId() + ": " + producto.getProductosXAlmacenes().size());

                List<ProductosXAlmacenDTO> productosXAlmacenDTOs = producto.getProductosXAlmacenes().stream()
                        .map(pa -> {
                            System.out.println("Mapeando ProductosXAlmacenEntity con ID: " + pa.getId());
                            try {
                                return modelMapper.map(pa, ProductosXAlmacenDTO.class);
                            } catch (Exception e) {
                                System.err.println("Error al mapear ProductosXAlmacenEntity a ProductosXAlmacenDTO: " + e.getMessage());
                                e.printStackTrace();
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)  // Para evitar agregar nulos a la lista
                        .collect(Collectors.toList());

                productoDTO.setProductosXAlmacen(productosXAlmacenDTOs);
            } else {
                System.out.println("Productos por almacén es nulo para producto ID: " + producto.getId());
            }

            return productoDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public Integer obtenerStockDisponible(Long id) {
        Optional<ProductosEntity> producto = productosRepository.findById(id);
        if (producto.isEmpty()) {
            throw new ProductoNotFoundException("El producto con ID " + id + " no existe");
        }
        ProductosEntity p = producto.get();
        ProductosXAlmacenEntity pa = p.getProductosXAlmacenes().stream().findFirst().get();
        return pa.getCantidad();
    }

    @RabbitListener(queues = "inventarioQueue")
    public void actualizarInventario(VentaCreadaEvent event) {
        try {
            // Actualizar el inventario según el evento de venta creada
            actualizarStock(event.getComprobanteDetalleRequest(), event.getIdAlmacen());

            System.out.println("Inventario actualizado para la venta: " + event.getIdComprobanteVenta());
        } catch (Exception e) {
            // Si falla, publicar un evento de compensación
            CompensarVentaEvent compensarEvent = CompensarVentaEvent.builder()
                    .ventaId(event.getIdComprobanteVenta())
                    .comprobanteDetalleRequest(event.getComprobanteDetalleRequest())
                    .idAlmacen(event.getIdAlmacen())
                    .build();
            rabbitTemplate.convertAndSend("ventasExchange", "venta.compensar", compensarEvent);

            System.err.println("Error al actualizar el inventario, se procede a compensar la venta: " + event.getIdComprobanteVenta());
        }
    }

    @RabbitListener(queues = "inventarioCompensarQueue")
    public void revertirInventario(CompensarVentaEvent event) {
        try {
            // Revertir el stock
            revertirStock(event.getComprobanteDetalleRequest(), event.getIdAlmacen());
            System.out.println("Inventario revertido para la venta compensada: " + event.getVentaId());
        } catch (Exception e) {
            System.err.println("Error al revertir el inventario: " + e.getMessage());
            throw new RuntimeException("Error al revertir el inventario: " + e.getMessage());
        }
    }

    @Transactional
    public void actualizarStock(List<ComprobanteDetalleRequest> comprobanteDetalleRequest, Long idAlmacen) {
        for (ComprobanteDetalleRequest detalle : comprobanteDetalleRequest) {
            ProductosXAlmacenEntity productoAlmacen = productosXAlmacenRepository.findByProductosIdAndAlmacenId(detalle.getIdProducto(), idAlmacen);

            if (productoAlmacen != null) {
                productoAlmacen.setCantidad(productoAlmacen.getCantidad() - detalle.getCantidad());
                productosXAlmacenRepository.save(productoAlmacen);
            } else {
                throw new ProductoNotFoundException("El producto no existe en el almacén especificado");
            }
        }
    }


    @Transactional
    public void revertirStock(List<ComprobanteDetalleRequest> comprobanteDetalleRequest, Long idAlmacen) {
        for (ComprobanteDetalleRequest detalle : comprobanteDetalleRequest) {
            ProductosXAlmacenEntity productoAlmacen = productosXAlmacenRepository.findByProductosIdAndAlmacenId(detalle.getIdProducto(), idAlmacen);

            if (productoAlmacen != null) {
                productoAlmacen.setCantidad(productoAlmacen.getCantidad() + detalle.getCantidad());

                productosXAlmacenRepository.save(productoAlmacen);
            } else {
                throw new ProductoNotFoundException("El producto no existe en el almacén especificado");
            }
        }
    }



}
