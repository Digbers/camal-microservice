package com.microservice.inventario.service.productos;

import com.microservice.inventario.clients.EmpresaClient;
import com.microservice.inventario.controller.DTO.*;
import com.microservice.inventario.event.*;
import com.microservice.inventario.exception.ProductoNotFoundException;
import com.microservice.inventario.persistence.entity.*;
import com.microservice.inventario.persistence.especification.ProductosSpecifications;
import com.microservice.inventario.persistence.repository.*;
import com.microservice.inventario.service.almacenI.StockAlmacenService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductosServiceImpl implements IProductosService {
    private final ProductosRepository productosRepository;
    private final IAlmacenRepository almacenRepository;
    private final IPuntoVentaRepository puntoVentaRepository;
    private final ModelMapper modelMapper;
    private final EmpresaClient empresaClient;
    private final ProductosTiposRepository tiposRepository;
    private final RabbitTemplate rabbitTemplate;
    //private final IProductosXAlmacenRepository productosXAlmacenRepository;
    private final IStockAlmacenRepository stockAlmacenRepository;
    private final EnvaseRepository envaseRepository;
    private final ProductosTiposRepository productosTiposRepository;
    private final StockAlmacenService stockAlmacenService;
    private final IMovimientosCabeceraRepository movimientosCabeceraRepository;
    private final IMovimientosMotivosRepository movimientosMotivosRepository;

    /**
     * Crear un nuevo producto y registrarlo en el almacén con stock inicial.
     *
     * @param producto     Entidad del producto a crear
     * @return El producto creado con su stock registrado
     */
    @Transactional
    @Override
    public ProductoDTO crearProductoYRegistrarStock(ProductoDTO producto) {
        // 1. Mapeo de ProductoDTO a ProductosEntity y guardado en la base de datos
        ProductosEntity productoGuardado = modelMapper.map(producto, ProductosEntity.class);
        ProductosEntity nuevoProducto = productosRepository.save(productoGuardado);

        if (producto.getStockAlmacenList() == null || producto.getStockAlmacenList().isEmpty()) {
            throw new IllegalArgumentException("La lista de productos por almacén no puede ser nula ni vacía.");
        }
        // 2. Iterar a través de la lista de stock para registrar cada uno en el almacén correspondiente
        for (StockAlmacenDTO stockDTO : producto.getStockAlmacenList()) {
            // Cargar el almacén usando el ID de la lista de StockAlmacenDTO
            AlmacenEntity almacen = almacenRepository.findById(stockDTO.getIdAlmacen())
                    .orElseThrow(() -> new EntityNotFoundException("Almacén no encontrado con ID: " + stockDTO.getIdAlmacen()));
            // Si hay un ID de envase, cargar el envase; si no, dejarlo como null
            EnvaseEntity envase = null;
            if (stockDTO.getIdEnvase() != null) {
                envase = envaseRepository.findById(stockDTO.getIdEnvase())
                        .orElseThrow(() -> new EntityNotFoundException("Envase no encontrado con ID: " + stockDTO.getIdEnvase()));
            }
            // 3. Crear el registro de stock en el almacén para el nuevo producto
            StockAlmacen stockAlmacen = StockAlmacen.builder()
                    .producto(nuevoProducto)
                    .almacen(almacen)
                    .envase(envase)
                    .cantidadEnvase(stockDTO.getCantidadEnvase())
                    .cantidadProducto(stockDTO.getCantidadProducto())
                    .pesoTotal(stockDTO.getPesoTotal())
                    .fechaRegistro(LocalDate.now())
                    .usuarioCreacion(nuevoProducto.getUsuarioCreacion())
                    .build();
            stockAlmacenRepository.save(stockAlmacen);
        }
        return modelMapper.map(nuevoProducto, ProductoDTO.class);
    }

    @Override
    public List<ProductoDTO> findByIdAlmacenProductoVenta(Long id) {
        return List.of();
    }

    @Override
    public ProductoDTO guardarConversion(ProductoDTO productoDTO) {
        return null;
    }
    @Override
    public Page<ProductoDTO> finAll(Long id, Long idEmpresa, String codigo, String nombre, String tipoId, Long almacenId, Pageable pageable) {
        Specification<ProductosEntity> specification = ProductosSpecifications.getProductos(id, idEmpresa, codigo, nombre, tipoId, almacenId);

        return productosRepository.findAll(specification, pageable).map(producto -> {
            ProductoDTO dtoProducto = modelMapper.map(producto, ProductoDTO.class);
            List<StockAlmacenDTO> stockAlmacenDTOs = producto.getStockAlmacenList().stream()
                    .map(sa -> modelMapper.map(sa, StockAlmacenDTO.class))
                    .collect(Collectors.toList());
            dtoProducto.setStockAlmacenList(stockAlmacenDTOs);
            return dtoProducto;
        });
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
    public Optional<ProductoDTO> findById(Long id) {

        Optional<ProductosEntity> producto1 = productosRepository.findById(id);
        if (producto1.isEmpty()) {
            return Optional.empty();
        }
        ProductosEntity producto = producto1.get();
        ProductoDTO dto = modelMapper.map(producto, ProductoDTO.class);
        List<StockAlmacenDTO> stockAlmacenDTOs = producto.getStockAlmacenList().stream()
                .map(sa -> modelMapper.map(sa, StockAlmacenDTO.class))
                .collect(Collectors.toList());
        dto.setStockAlmacenList(stockAlmacenDTOs);
        return Optional.of(dto);
    }
    @Override
    public List<ProductoDTO> findAll() {

        return productosRepository.findAll().stream()
                .peek(producto -> {
                    // Forzamos la carga de los productos por almacén
                    producto.getStockAlmacenList().forEach(pa -> {
                        pa.getAlmacen().getId();  // Cargar Almacen (LAZY)
                        pa.getProducto().getIdProducto(); // Cargar Producto (LAZY)
                    });
                })
                .map(producto -> {
                    ProductoDTO dto = modelMapper.map(producto, ProductoDTO.class);
                    // Mapear manualmente la lista de productos por almacén
                    List<StockAlmacenDTO> stockAlmacenDTOs = producto.getStockAlmacenList().stream()
                            .map(sa -> modelMapper.map(sa, StockAlmacenDTO.class))  // Mapear a DTO
                            .collect(Collectors.toList());  // Convertir a lista
                    dto.setStockAlmacenList(stockAlmacenDTOs);
                    return dto;
                })
                .collect(Collectors.toList());
    }
    @Override
    public List<ProductoDTO> findByDescripcionAutocomplete(String descripcion) {
        try {
            List<ProductosEntity> productos = productosRepository.searchByFields(descripcion);
            return productos.stream()
                    .peek(producto -> {
                        // Forzamos la carga de los productos por almacén
                        producto.getStockAlmacenList().forEach(pa -> {
                            pa.getAlmacen().getId();  // Cargar Almacen (LAZY)
                            pa.getProducto().getIdProducto(); // Cargar Producto (LAZY)
                        });
                    })
                    .map(producto -> {
                        ProductoDTO dto = modelMapper.map(producto, ProductoDTO.class);
                        List<StockAlmacenDTO> productosXAlmacenDTOs = producto.getStockAlmacenList().stream()
                                .map(pa -> modelMapper.map(pa, StockAlmacenDTO.class))
                                .collect(Collectors.toList());
                        dto.setStockAlmacenList(productosXAlmacenDTOs);
                        return dto;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error al buscar productos por descripción: " + e.getMessage());
            throw new ProductoNotFoundException("No se pudo encontrar el producto con descripción " + descripcion);
        }
    }

    @Override
    public ProductoDTO update(Long id, ProductoDTO productoRequest) {
        ProductosEntity producto = productosRepository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException("Producto no encontrado"));
        ProductosTiposEntity tipo = productosTiposRepository.findById(productoRequest.getTipo().getCodigo())
                .orElseThrow(() -> new ProductoNotFoundException("Tipo de producto no encontrado"));
        List<StockAlmacen> stockAlmacenList = producto.getStockAlmacenList().stream()
                .map(sa -> modelMapper.map(sa, StockAlmacen.class))
                .collect(Collectors.toList());
        producto.setCodigo(productoRequest.getCodigo());
        producto.setNombre(productoRequest.getNombre());
        producto.setTipo(tipo);
        producto.setStockAlmacenList(stockAlmacenList);
        productosRepository.save(producto);
        return modelMapper.map(producto, ProductoDTO.class);
    }

    @Override
    public ProductoDTO save(ProductoDTO productoRequest) {
        ProductosEntity producto = modelMapper.map(productoRequest, ProductosEntity.class);
        ProductosTiposEntity tipo = productosTiposRepository.findById(productoRequest.getTipo().getCodigo())
                .orElseThrow(() -> new ProductoNotFoundException("Tipo de producto no encontrado"));
        List<StockAlmacen> stockAlmacenList = producto.getStockAlmacenList().stream()
                .map(sa -> modelMapper.map(sa, StockAlmacen.class))
                .collect(Collectors.toList());
        producto.setTipo(tipo);
        producto.setStockAlmacenList(stockAlmacenList);
        productosRepository.save(producto);
        return modelMapper.map(producto, ProductoDTO.class);
    }
    @RabbitListener(queues = "VentaCreadaQueue")
    public void actualizarInventario(VentaCreadaEvent event) {
        try {
            // Actualizar el inventario según el evento de venta creada
            List<ComprobanteDetalleRequest> comprobanteDetalle = Optional.ofNullable(event.getComprobantesVentasCab())
                    .map(ca -> ca.getComprobantesVentaDet())
                    .orElse(Collections.emptyList()) // Devuelve una lista vacía si el detalle es null
                    .stream()
                    .map(cd -> new ComprobanteDetalleRequest(cd.getCantidad(),cd.getIdProducto(),cd.getIdEnvase(),cd.getPeso(),cd.getPrecioUnitario(),cd.getDescuento()))
                    .collect(Collectors.toList());
            log.info("Inventario actualizado para la venta: " + event.getComprobantesVentasCab().getId());
            actualizarStock(comprobanteDetalle, event.getIdAlmacen(), event.getComprobantesVentasCab().getIdEmpresa());

            AlmacenEntity almacen = almacenRepository.findById(event.getIdAlmacen()).orElseThrow(() -> new EntityNotFoundException("Almacén no encontrado"));

            MovimientosMotivosEntity motivo  = movimientosMotivosRepository.findById("VEN").orElseThrow(() -> new EntityNotFoundException("Motivo no encontrado"));
            BigDecimal totalR = calcularMontoTotal(comprobanteDetalle);
            Long lastNumber = movimientosCabeceraRepository.findMaxNumber();
            AtomicReference<Integer> cantidadEnvaces = new AtomicReference<>(0);
            MovimientosCabeceraDTO movimiento = MovimientosCabeceraDTO.builder()
                    .idEmpresa(event.getComprobantesVentasCab().getIdEmpresa())
                    .numero(String.valueOf(lastNumber + 1))
                    .fechaEmision(LocalDate.now())
                    .total(totalR)
                    .motivoCodigo(modelMapper.map(motivo, MovimientosMotivosDTO.class))
                    .idUsuario(event.getComprobantesVentasCab().getUsuarioCreacion())
                    .monedaCodigo(event.getComprobantesVentasCab().getCodigoMoneda())
                    .movimientosDetalles(comprobanteDetalle.stream()
                            .map(cd -> {
                                cantidadEnvaces.getAndSet(cantidadEnvaces.get() + cd.getCantidad());
                                return  MovimientosDetalleDTO.builder()
                                    .idEmpresa(event.getComprobantesVentasCab().getIdEmpresa())
                                    .idProducto(cd.getIdProducto())
                                    .envase(cd.getIdEnvase())
                                    .peso(cd.getPeso())
                                    .total(BigDecimal.valueOf(cd.getCantidad()).multiply(cd.getPrecioUnitario()))
                                    .cantidad(cd.getCantidad())
                                    .usuarioCreacion(event.getComprobantesVentasCab().getUsuarioCreacion())
                                    .build();
                            })
                            .collect(Collectors.toList()))
                    .idAlmacen(modelMapper.map(almacen, AlmacenDTO.class))
                    .tipoDocumentoReferencia(event.getComprobantesVentasCab().getComprobantesTipos().getCodigo())
                    .serieDocumentoReferencia(event.getComprobantesVentasCab().getSerie())
                    .numeroDocumentoReferencia(event.getComprobantesVentasCab().getNumero())
                    .observaciones(event.getComprobantesVentasCab().getObservacion())
                    .idEntidad(event.getComprobantesVentasCab().getIdCliente())
                    .cantidadEnvaces(cantidadEnvaces.get())
                    .fechaIngresoSalida(event.getComprobantesVentasCab().getFechaEmision())
                    .usuarioCreacion(event.getComprobantesVentasCab().getUsuarioCreacion())
                    .build();
            boolean resutl = stockAlmacenService.crearMovimiento(movimiento);
            if(!resutl){
                throw new RuntimeException("Error al crear movimiento");
            }
            log.info("Movimiento creado para la venta: " + event.getComprobantesVentasCab().getId());
            InventarioActualizadoVentasEvent actualizacionEvent = InventarioActualizadoVentasEvent.builder()
                    .comprobantesVentasCab(event.getComprobantesVentasCab())
                    .codigoFormaPago(event.getCodigoFormaPago())
                    .idAlmacen(event.getIdAlmacen())
                    .montoTotal(totalR)
                    .build();
            rabbitTemplate.convertAndSend("InventarioVentasExchange", "inventario.actualizado-ventas", actualizacionEvent);

        } catch (Exception e) {
            // Si falla, publicar un evento de compensación
            VentaFailedEvent compensarEvent = VentaFailedEvent.builder()
                    .id(event.getComprobantesVentasCab().getId())
                    .source("inventario")
                    .build();
            rabbitTemplate.convertAndSend("InventarioVentasExchange", "inventario.erroractualizado-ventas", compensarEvent);
            log.error("Error al actualizar el inventario, se procede a compensar la venta: " + event.getComprobantesVentasCab().getId());
        }
    }

    @RabbitListener(queues = "InventarioCompensarQueue")
    public void revertirInventario(CompensarVentaEvent event) {
        try {
            if (!event.getSource().equals("inventario")) {
                Optional<AlmacenEntity> almacen = puntoVentaRepository.findByIdAlmacen(event.getIdPuntoVenta());
                if (almacen.isEmpty()) {
                    throw new RuntimeException("No se encontro el almacen con ID: " + event.getIdPuntoVenta());
                }
                revertirStock(event.getComprobanteDetalleRequest(), almacen.get().getId(), event.getIdEmpresa());
                log.info("Inventario revertido para la venta compensada: " + event.getVentaId());
            } else {
                log.info("Ignorando compensación en Inventario porque la fuente de error es: " + event.getSource());
            }
        } catch (Exception e) {
            log.error("Error al revertir el inventario: " + e.getMessage());
            throw new RuntimeException("Error al revertir el inventario: " + e.getMessage());
        }
    }
    @RabbitListener(queues = "CompraCreadaQueue")
    public void handleCompraCreada(CompraCreadaEvent event) {
        try {
            List<ComprobanteDetalleRequest> comprobanteDetalle = Optional.ofNullable(event.getComprobantesComprasCa())
                    .map(ca -> ca.getComprobantesComprasDetalle())
                    .orElse(Collections.emptyList()) // Devuelve una lista vacía si el detalle es null
                    .stream()
                    .map(cd -> new ComprobanteDetalleRequest(cd.getCantidad(),cd.getIdProducto(),cd.getIdEnvase(),cd.getPeso(),cd.getPrecioUnitario(),cd.getDescuento()))
                    .collect(Collectors.toList());

            actualizarStock(comprobanteDetalle, event.getIdAlmacen(), event.getComprobantesComprasCa().getIdEmpresa());

            AlmacenEntity almacen = almacenRepository.findById(event.getIdAlmacen()).orElseThrow(() -> new EntityNotFoundException("Almacén no encontrado"));

            MovimientosMotivosEntity motivo  = movimientosMotivosRepository.findById("COM").orElseThrow(() -> new EntityNotFoundException("Motivo no encontrado"));
            BigDecimal totalR = calcularMontoTotal(comprobanteDetalle);
            Long lastNumber = movimientosCabeceraRepository.findMaxNumber();
            AtomicReference<Integer> cantidadEnvaces = new AtomicReference<>(0);
            MovimientosCabeceraDTO movimiento = MovimientosCabeceraDTO.builder()
                    .idEmpresa(event.getComprobantesComprasCa().getIdEmpresa())
                    .numero(String.valueOf(lastNumber + 1))
                    .fechaEmision(LocalDate.now())
                    .total(totalR)
                    .motivoCodigo(modelMapper.map(motivo, MovimientosMotivosDTO.class))
                    .idUsuario(event.getComprobantesComprasCa().getUsuarioCreacion())
                    .monedaCodigo(event.getComprobantesComprasCa().getCodigoMoneda())
                    .movimientosDetalles(comprobanteDetalle.stream()
                            .map(cd -> {
                                cantidadEnvaces.getAndSet(cantidadEnvaces.get() + cd.getCantidad());
                                return  MovimientosDetalleDTO.builder()
                                        .idEmpresa(event.getComprobantesComprasCa().getIdEmpresa())
                                        .idProducto(cd.getIdProducto())
                                        .envase(cd.getIdEnvase())
                                        .peso(cd.getPeso())
                                        .total(BigDecimal.valueOf(cd.getCantidad()).multiply(cd.getPrecioUnitario()))
                                        .cantidad(cd.getCantidad())
                                        .usuarioCreacion(event.getComprobantesComprasCa().getUsuarioCreacion())
                                        .build();
                            })
                            .collect(Collectors.toList()))
                    .idAlmacen(modelMapper.map(almacen, AlmacenDTO.class))
                    .tipoDocumentoReferencia(event.getComprobantesComprasCa().getComprobantesTipos().getCodigo())
                    .serieDocumentoReferencia(event.getComprobantesComprasCa().getSerie())
                    .numeroDocumentoReferencia(event.getComprobantesComprasCa().getNumero())
                    .observaciones(event.getComprobantesComprasCa().getObservacion())
                    .idEntidad(event.getComprobantesComprasCa().getIdProveedor())
                    .cantidadEnvaces(cantidadEnvaces.get())
                    .fechaIngresoSalida(event.getComprobantesComprasCa().getFechaEmision())
                    .usuarioCreacion(event.getComprobantesComprasCa().getUsuarioCreacion())
                    .build();
            boolean resutl = stockAlmacenService.crearMovimiento(movimiento);
            if(!resutl){
                throw new RuntimeException("Error al crear movimiento");
            }
            log.info("Movimiento creado para la venta: " + event.getComprobantesComprasCa().getId());

            InventarioActualizadoCompraEvent inventarioEvent = InventarioActualizadoCompraEvent.builder()
                    .idAlmacen(event.getIdAlmacen())
                    .comprobantesComprasCa(event.getComprobantesComprasCa())
                    .codigoFormaPago(event.getCodigoFormaPago())
                    .montoTotal(calcularMontoTotal(comprobanteDetalle))
                    .build();
            rabbitTemplate.convertAndSend("InventarioExchange", "inventario.actualizado", inventarioEvent);
        } catch (Exception e) {
            log.error("Error al actualizar el inventario: " + e.getMessage());
            CompraFailedEvent compensacion = CompraFailedEvent.builder()
                    .id(event.getComprobantesComprasCa().getId())
                    .source("inventario")
                    .build();
            rabbitTemplate.convertAndSend("InventarioExchange", "inventario.erroractualizado", compensacion);
        }
    }
    //FALTA ESTA PARTE
    @RabbitListener(queues = "VentasCompensarQueue")
    public void handleVentaCompensacion(CompensacionCompraEvent event) {
        if (!event.getSource().equals("inventario")) {
            log.info("Compensación en inventario para la venta: " + event.getId());
            try {
                if(!event.getSource().equals("inventario")){
                    Optional<AlmacenEntity> almacen = puntoVentaRepository.findByIdAlmacen(event.getIdPuntoVenta());
                    if (almacen.isEmpty()) {
                        throw new RuntimeException("No se encontro el almacen con ID: " + event.getIdPuntoVenta());
                    }
                    revertirStock(event.getComprobanteDetalleRequest(), almacen.get().getId(), event.getIdEmpresa());
                    log.info("Inventario revertido para la venta compensada: " + event.getId());

                }
            } catch (Exception e) {
                log.error("Error al revertir el inventario: " + e.getMessage());
                throw new RuntimeException("Error al revertir el inventario: " + e.getMessage());
            }
        } else {
            log.info("Ignorando compensación en Inventario porque la fuente de error es: " + event.getSource());
        }
    }
    @RabbitListener(queues = "CompensacionCompraQueue")
    public void handleCompensacionCompra(CompensacionCompraEvent event) {
        if (!event.getSource().equals("inventario")) {
            log.info("Compensación en Inventario para la compra: " + event.getId());
            try {
                Optional<AlmacenEntity> almacen = puntoVentaRepository.findByIdAlmacen(event.getIdPuntoVenta());
                if (almacen.isEmpty()) {
                    throw new RuntimeException("No se encontro el almacen con ID: " + event.getIdPuntoVenta());
                }
                revertirStock(event.getComprobanteDetalleRequest(), almacen.get().getId(), event.getIdEmpresa());
                log.info("Inventario revertido para la venta compensada: " + event.getId());
            } catch (Exception e) {
                log.error("Error al revertir el inventario: " + e.getMessage());
                throw new RuntimeException("Error al revertir el inventario: " + e.getMessage());
            }
        } else {
            log.info("Ignorando compensación en Inventario porque la fuente de error es: " + event.getSource());
        }
    }
    private BigDecimal calcularMontoTotal(List<ComprobanteDetalleRequest> detalleComprobantes) {
        BigDecimal montoTotal = BigDecimal.ZERO;
        for (ComprobanteDetalleRequest comprobanteDetalle : detalleComprobantes) {
            if (comprobanteDetalle.getCantidad() != null && comprobanteDetalle.getPrecioUnitario() != null) {
                BigDecimal subtotal = BigDecimal.valueOf(comprobanteDetalle.getCantidad()).multiply(comprobanteDetalle.getPrecioUnitario());
                montoTotal = montoTotal.add(subtotal);
            }else{
                log.error("El detalle no tiene cantidad o precio");
                throw new RuntimeException("El detalle no tiene cantidad o precio");
            }
        }
        return montoTotal;
    }

    @Transactional
    public void actualizarStock(List<ComprobanteDetalleRequest> comprobanteDetalleRequest, Long idAlmacen, Long idEmpresa) {
        for (ComprobanteDetalleRequest detalle : comprobanteDetalleRequest) {
            StockAlmacen stockAlmacen = stockAlmacenRepository.findByIds(idAlmacen, detalle.getIdProducto(), detalle.getIdEnvase(), idEmpresa).get();
            if (stockAlmacen != null) {
                stockAlmacen.setCantidadProducto(stockAlmacen.getCantidadProducto() - detalle.getCantidad());
                stockAlmacenRepository.save(stockAlmacen);
                log.info("Producto actualizado en el almacén: " + detalle.getIdProducto() + " por " + detalle.getCantidad());
            } else {
                throw new ProductoNotFoundException("El producto no existe en el almacén especificado");
            }
        }
    }

    @Transactional
    public void revertirStock(List<ComprobanteDetalleRequest> comprobanteDetalleRequest, Long idAlmacen, Long idEmpresa) {
        for (ComprobanteDetalleRequest detalle : comprobanteDetalleRequest) {
            StockAlmacen stockAlmacen = stockAlmacenRepository.findByIds(idAlmacen, detalle.getIdProducto(), detalle.getIdEnvase(), idEmpresa).get();
            if (stockAlmacen != null) {
                stockAlmacen.setCantidadProducto(stockAlmacen.getCantidadProducto() + detalle.getCantidad());
                stockAlmacenRepository.save(stockAlmacen);
                log.info("Producto revertido en el almacén: " + detalle.getIdProducto() + " por " + detalle.getCantidad() + " cantidad, " + detalle.getIdEnvase() + " envase.");
            } else {
                throw new ProductoNotFoundException("El producto no existe en el almacén especificado");
            }
        }
    }



}
