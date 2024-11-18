package com.microservice.inventario.service.productos;

import com.microservice.inventario.clients.EmpresaClient;
import com.microservice.inventario.controller.DTO.*;
import com.microservice.inventario.controller.DTO.compras.ComprobantesComprasCaDTO;
import com.microservice.inventario.controller.DTO.compras.ComprobantesComprasTiposDTO;
import com.microservice.inventario.controller.DTO.response.ProductoAResponse;
import com.microservice.inventario.controller.DTO.ventas.ComprobantesVentasCabDTO;
import com.microservice.inventario.event.*;
import com.microservice.inventario.exception.ProductoNotFoundException;
import com.microservice.inventario.persistence.entity.*;
import com.microservice.inventario.persistence.especification.ProductosSpecifications;
import com.microservice.inventario.persistence.repository.*;
import com.microservice.inventario.service.almacenI.StockAlmacenService;
import com.rabbitmq.client.Channel;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
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
    private final IUnidadesRepository unidadesRepository;
    private final RabbitTemplate rabbitTemplate;
    private final IStockAlmacenRepository stockAlmacenRepository;
    private final EnvaseRepository envaseRepository;
    private final ProductosTiposRepository productosTiposRepository;
    private final StockAlmacenService stockAlmacenService;
    private final IMovimientosCabeceraRepository movimientosCabeceraRepository;
    private final IMovimientosMotivosRepository movimientosMotivosRepository;

    @Override
    public List<ProductoDTO> findByIdAlmacenProductoVenta(Long id) {
        return List.of();
    }

    @Override
    @Transactional
    public Boolean convertirProducto(Long idEmpresa, Long idAlmacen, Long idProducto, Long idEnvase, Integer cantidad, String codigoProductoConvert) {
        try {
            Optional<StockAlmacen> stockAlmacen = stockAlmacenRepository.findByIds(idAlmacen, idProducto, idEnvase, idEmpresa);
            if (stockAlmacen.isEmpty()) {
                throw new ProductoNotFoundException("El producto no existe en el almacén especificado");
            }
            if (stockAlmacen.get().getCantidadProducto() < cantidad) {
                throw new RuntimeException("No se puede convertir el producto porque el stock es inferior al cantidad a convertir");
            }
            StockAlmacen stockAlmacenUpdated = stockAlmacen.get();

            //buscar el prodcuto con el codigo especificado pollo sacrificado
            Optional<ProductosEntity> productosEntity = productosRepository.findByCodigo(codigoProductoConvert);
            if(productosEntity.isEmpty()){
                throw new RuntimeException("No se encontro el producto con el codigo especificado: " + codigoProductoConvert);
            }
            ProductosEntity productosEntityUpdated = productosEntity.get();
            Optional<StockAlmacen> stockAlmacen2 = productosEntityUpdated
                    .getStockAlmacenList()
                    .stream()
                    .filter(stock -> stock.getEnvase() == null)// EL PRODCUTO POLLO SACRIFICADO NO TINE ENVASE
                    .findFirst();
            if(stockAlmacen2.isEmpty()){
                throw new RuntimeException("No se encontro el stock de producto: " + codigoProductoConvert);
            }
            //actualizar el stock del producto
            stockAlmacenUpdated.setCantidadProducto(stockAlmacenUpdated.getCantidadProducto() - cantidad);
            stockAlmacenRepository.save(stockAlmacenUpdated);
            StockAlmacen stockAlmacenReceived = stockAlmacen2.get();
            stockAlmacenReceived.setCantidadProducto(stockAlmacen2.get().getCantidadProducto() + cantidad);
            stockAlmacenRepository.save(stockAlmacenReceived);
            return true;
        } catch (Exception e) {
            log.error("Error al convertir producto: " + e.getMessage());
            throw new ProductoNotFoundException("Error al convertir producto: " + e.getMessage());
        }
    }

    @Override
    public Page<ProductoDTO> finAll(Long id, Long idEmpresa, String codigo, String nombre, String tipoId, Long almacenId, Pageable pageable) {
        try {
            Specification<ProductosEntity> specification = ProductosSpecifications.getProductos(id, idEmpresa, codigo, nombre, tipoId, almacenId);
            List<ProductoDTO> productoDTOList = new ArrayList<>();

            productosRepository.findAll(specification, pageable).forEach(producto -> {

                // Iterar sobre cada StockAlmacen y crear un ProductoDTO por cada uno
                producto.getStockAlmacenList().forEach(stockAlmacen -> {
                    ProductoDTO dtoProducto = modelMapper.map(producto, ProductoDTO.class);
                    dtoProducto.setPrecioSugerido(producto.getPrecioSugerido());
                    dtoProducto.setStockAlmacenId(stockAlmacen.getIdStock());
                    dtoProducto.setAlmacenId(stockAlmacen.getAlmacen().getId());
                    dtoProducto.setEnvaseId(stockAlmacen.getEnvase() != null ? stockAlmacen.getEnvase().getIdEnvase() : null);
                    dtoProducto.setCantidadEnvase(stockAlmacen.getCantidadEnvase());
                    dtoProducto.setCantidadProducto(stockAlmacen.getCantidadProducto());
                    dtoProducto.setPesoTotal(stockAlmacen.getPesoTotal());
                    dtoProducto.setFechaRegistro(stockAlmacen.getFechaRegistro());

                    productoDTOList.add(dtoProducto);
                });
            });

            // Convertir la lista de ProductoDTO a Page
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), productoDTOList.size());
            return new PageImpl<>(productoDTOList.subList(start, end), pageable, productoDTOList.size());
        } catch (Exception e) {
            log.error("Error al buscar productos: " + e.getMessage());
            throw new ProductoNotFoundException("No se pudo encontrar el producto: " + e.getMessage());
        }
    }
    @Override
    @Transactional
    public Boolean deleteById(Long id) {
        //falts corregir
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
        producto.getStockAlmacenList().stream()
                .forEach(sa -> {
                    dto.setStockAlmacenId(sa.getIdStock());
                    dto.setAlmacenId(sa.getAlmacen().getId());
                    dto.setEnvaseId(sa.getEnvase().getIdEnvase());
                    dto.setCantidadEnvase(sa.getCantidadEnvase());
                    dto.setCantidadProducto(sa.getCantidadProducto());
                    dto.setPesoTotal(sa.getPesoTotal());
                    dto.setFechaRegistro(sa.getFechaRegistro());
                });
        return Optional.of(dto);
    }
    @Override
    public List<ProductoDTO> findAll() {
        try {
            // Obtener todos los productos
            List<ProductosEntity> productos = productosRepository.findAll();
            // Extraer los IDs de las empresas de todos los productos

            List<ProductoDTO> productoDTOList = new ArrayList<>();
            // Iterar sobre todos los productos
            productos.forEach(producto -> {
                // Iterar sobre cada StockAlmacen del producto
                producto.getStockAlmacenList().forEach(stockAlmacen -> {
                    // Mapear el producto a ProductoDTO
                    ProductoDTO dtoProducto = modelMapper.map(producto, ProductoDTO.class);
                    dtoProducto.setStockAlmacenId(stockAlmacen.getIdStock());
                    dtoProducto.setAlmacenId(stockAlmacen.getAlmacen().getId());
                    dtoProducto.setEnvaseId(stockAlmacen.getEnvase().getIdEnvase());
                    dtoProducto.setCantidadEnvase(stockAlmacen.getCantidadEnvase());
                    dtoProducto.setCantidadProducto(stockAlmacen.getCantidadProducto());
                    dtoProducto.setPesoTotal(stockAlmacen.getPesoTotal());
                    dtoProducto.setFechaRegistro(stockAlmacen.getFechaRegistro());

                    // Agregar el ProductoDTO a la lista final
                    productoDTOList.add(dtoProducto);
                });
            });
            return productoDTOList;
        } catch (Exception e) {
            log.error("Error al obtener productos: " + e.getMessage());
            throw new ProductoNotFoundException("Error al obtener productos: " + e.getMessage());
        }
    }
    @Override
    public List<ProductoAResponse> findByDescripcionAutocomplete(String descripcion) {
        try {
            // List para almacenar todos los productos aplanados
            List<ProductoAResponse> productos = new ArrayList<>();

            productosRepository.searchByFields(descripcion).forEach(producto -> {
                // Iterar sobre cada StockAlmacen y crear un ProductoDTO por cada uno
                producto.getStockAlmacenList().forEach(stockAlmacen -> {
                    if (stockAlmacen.getEnvase() != null) {
                        ProductoAResponse productoAResponse = ProductoAResponse.builder()
                                .id(producto.getIdProducto())
                                .empresaId(producto.getEmpresaId())
                                .codigo(producto.getCodigo())
                                .descripcionA(producto.getCodigo() + " - " + producto.getNombre() + " - " + producto.getTipo().getNombre()
                                        + " - " + producto.getUnidad().getCodigo() + " - " + producto.getPrecioSugerido()
                                        + " - " + (stockAlmacen.getEnvase() != null ? stockAlmacen.getEnvase().getDescripcion() : "Sin envase"))
                                .nombre(producto.getCodigo() + " - " + producto.getNombre() + " - " + producto.getTipo().getNombre())
                                .unidad(producto.getUnidad().getCodigo())
                                .cantidad(stockAlmacen.getCantidadProducto())
                                .precio(producto.getPrecioSugerido())
                                .envaseId(stockAlmacen.getEnvase() != null ? stockAlmacen.getEnvase().getIdEnvase() : null)
                                .capacidadEnvase(stockAlmacen.getEnvase() != null ? stockAlmacen.getEnvase().getCapacidad() : null)
                                .peso(stockAlmacen.getPesoTotal())
                                .build();
                        productos.add(productoAResponse);
                    }
                });
            });
            return productos;
        } catch (Exception e) {
            log.error("Error al buscar productos por descripción: " + e.getMessage());
            throw new ProductoNotFoundException("No se pudo encontrar el producto con descripción " + descripcion);
        }
    }
    @Override
    public ProductoDTO update(Long id, ProductoDTO productoRequest) {
        ProductosEntity producto = productosRepository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException("Producto no encontrado"));
        return saveOrUpdateProducto(productoRequest, producto, "update");
    }
    @Override
    public ProductoDTO save(ProductoDTO productoRequest) {
        ProductosEntity producto = new ProductosEntity();
        return saveOrUpdateProducto(productoRequest, producto, "save");
    }

    private ProductoDTO saveOrUpdateProducto(ProductoDTO productoRequest, ProductosEntity producto, String action) {
        try {
            StockAlmacen stockAlmacen = null;
            ProductosTiposEntity tipo = productosTiposRepository.findByIdEmpresaAndCodigo(productoRequest.getEmpresa(), productoRequest.getTipo())
                    .orElseThrow(() -> new ProductoNotFoundException("Tipo de producto no encontrado"));
            AlmacenEntity almacen = almacenRepository.findById(productoRequest.getAlmacenId())
                    .orElseThrow(() -> new EntityNotFoundException("Almacén no encontrado con ID: " + productoRequest.getAlmacenId()));
            EnvaseEntity envase = envaseRepository.findById(productoRequest.getEnvaseId())
                    .orElseThrow(() -> new EntityNotFoundException("Envase no encontrado con ID: " + productoRequest.getEnvaseId()));
            UnidadesEntity unidad = unidadesRepository.findByIdEmpresaAndCodigo(productoRequest.getEmpresa(), productoRequest.getUnidad())
                    .orElseThrow(() -> new EntityNotFoundException("Unidad no encontrado con ID: " + productoRequest.getUnidad()));
            if (action.equals("save")) {
                log.info("Guardando producto");
                producto.setUsuarioCreacion(productoRequest.getUsuarioCreacion());
                // Creando nuevo StockAlmacen
                stockAlmacen = StockAlmacen.builder()
                        .idEmpresa(productoRequest.getEmpresa())
                        .almacen(almacen)
                        .envase(envase)
                        .cantidadEnvase(productoRequest.getCantidadEnvase())
                        .cantidadProducto(productoRequest.getCantidadProducto())
                        .pesoTotal(productoRequest.getPesoTotal())
                        .fechaRegistro(LocalDate.now())
                        .usuarioCreacion(productoRequest.getUsuarioCreacion())
                        .build();
                producto.setStockAlmacenList(new ArrayList<>());
                producto.getStockAlmacenList().add(stockAlmacen);
            } else {
                log.info("Actualizando producto");
                producto.setUsuarioActualizacion(productoRequest.getUsuarioActualizacion());

                stockAlmacen = stockAlmacenRepository.findById(productoRequest.getStockAlmacenId())
                        .orElseThrow(() -> new EntityNotFoundException("Stock no encontrado con ID: " + productoRequest.getStockAlmacenId()));
                stockAlmacen.setAlmacen(almacen);
                stockAlmacen.setEnvase(envase);
                stockAlmacen.setCantidadEnvase(productoRequest.getCantidadEnvase());
                stockAlmacen.setCantidadProducto(productoRequest.getCantidadProducto());
                stockAlmacen.setPesoTotal(productoRequest.getPesoTotal());
                stockAlmacen.setFechaRegistro(LocalDate.now());
                stockAlmacen.setUsuarioActualizacion(productoRequest.getUsuarioActualizacion());
            }
            // Actualizar atributos del producto
            producto.setUnidad(unidad);
            producto.setEmpresaId(productoRequest.getEmpresa());
            producto.setCodigo(productoRequest.getCodigo());
            producto.setNombre(productoRequest.getNombre());
            producto.setTipo(tipo);
            producto.setGenerarStock(productoRequest.getGenerarStock());
            producto.setEstado(productoRequest.getEstado());
            producto.setPrecioSugerido(productoRequest.getPrecioSugerido());
            // Vincular StockAlmacen al Producto
            stockAlmacen.setProducto(producto);

            // Guardar producto (y en cascada se guarda StockAlmacen)
            productosRepository.save(producto);

            // Mapeo y retorno del DTO
            return modelMapper.map(producto, ProductoDTO.class);

        } catch (Exception e) {
            log.error("Error al guardar producto: " + e.getMessage());
            throw new ProductoNotFoundException("Error al guardar producto: " + e.getMessage());
        }
    }

    @RabbitListener(queues = "VentaCreadaQueue")
    @Transactional
    public void actualizarInventarioVenta(VentaCreadaEvent event, Channel channel, Message message) throws IOException {
        try {
            log.info("Recibido evento de venta creada");
            Boolean generarMovimiento = false;
            actualizarInventarioComun(event.getComprobantesVentasCab(), event.getIdAlmacen(), "VEN", event.getCodigoFormaPago(),
                    event.getComprobantesVentasCobrosDTO(), channel, message, generarMovimiento, event.getCodigoProductoVenta());
        } catch (Exception e) {
            e.printStackTrace();
            manejarError(event.getCodigoProductoVenta(),event.getComprobantesVentasCab().getId(),"VEN","InventarioVentasExchange", "inventario.erroractualizado-ventas", "inventario", e, channel, message);
        }
    }

    @RabbitListener(queues = "CompraCreadaQueue")
    public void actualizarInventarioCompra(CompraCreadaEvent event, Channel channel, Message message) throws IOException {
        try {
            log.info("Recibido evento de compra creada");
            Boolean generarMovimiento = event.getGenerarMovimiento();
            actualizarInventarioComun(event.getComprobantesComprasCa(), event.getIdAlmacen(), "COM", event.getCodigoFormaPago(),
                        event.getComprobantesComprasPagosDTO(), channel, message, generarMovimiento, event.getCodigoProductoCompra());
        } catch (Exception e) {
            e.printStackTrace();
            manejarError(event.getCodigoProductoCompra(),event.getComprobantesComprasCa().getId(),"COM","InventarioCompraExchange", "inventario.error-actualizando-c", "inventario", e, channel, message);
        }
    }
    private <T> void actualizarInventarioComun(ComprobanteCabeceraDTO<?> comprobantesCabecera, Long idAlmacen, String motivoCodigo,
                                               String codigoFormaPago, List<T> cobrosDTO, Channel channel, Message message, Boolean generarMovimiento, String codigoProductoCompraVenta) throws IOException {
        // Obtener detalles del comprobante
        List<ComprobanteDetalleRequest> comprobanteDetalle = obtenerDetalleComprobante(comprobantesCabecera);
        BigDecimal total = calcularMontoTotal(comprobanteDetalle);
        if(motivoCodigo.equals("COM") && !generarMovimiento) {
            log.info("No se generara movimiento");
            // Enviar evento de éxito (aquí podrías tener lógica diferente para ventas y compras si es necesario)
            enviarEventoExito(comprobantesCabecera, idAlmacen, codigoFormaPago, total, cobrosDTO, motivoCodigo);
            // Confirmar el mensaje
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }else{

            actualizarStock(comprobanteDetalle, idAlmacen, comprobantesCabecera.getIdEmpresa(), motivoCodigo, codigoProductoCompraVenta);
            // Obtener información del almacén y motivo
            AlmacenEntity almacen = obtenerAlmacen(idAlmacen);
            MovimientosMotivosEntity motivo = obtenerMotivo(motivoCodigo);

            // Crear movimiento
            MovimientosCabeceraEntity movimiento = crearMovimientoCabecera(comprobantesCabecera, comprobanteDetalle, almacen, motivo, total);

            // Crear movimiento en el sistema
            if (!stockAlmacenService.crearMovimiento(movimiento)) {
                throw new RuntimeException("Error al crear movimiento");
            }
            log.info("Movimiento creado para la" + motivoCodigo + ": " + comprobantesCabecera.getId());
        }
        // Enviar evento de éxito (aquí podrías tener lógica diferente para ventas y compras si es necesario)
        enviarEventoExito(comprobantesCabecera, idAlmacen, codigoFormaPago, total, cobrosDTO, motivoCodigo);
        // Confirmar el mensaje
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
    private <T> void enviarEventoExito(ComprobanteCabeceraDTO<?> comprobantesCabecera, Long idAlmacen, String codigoFormaPago,
                                       BigDecimal montoTotal, List<T> cobrosDTO, String motivoCodigo) {
        if (motivoCodigo.equals("VEN")) {
            ComprobantesVentasCabDTO comprobantesVentasCabDTO = (ComprobantesVentasCabDTO) comprobantesCabecera;
            List<ComprobantesVentasCobrosEventDTO> ventasCobrosDTO = (List<ComprobantesVentasCobrosEventDTO>) cobrosDTO;
            InventarioActualizadoVentasEvent actualizacionEvent = InventarioActualizadoVentasEvent.builder()
                    .comprobantesVentasCab(comprobantesVentasCabDTO)
                    .codigoFormaPago(codigoFormaPago)
                    .idAlmacen(idAlmacen)
                    .montoTotal(montoTotal)
                    .comprobantesVentasCobrosDTO(ventasCobrosDTO)
                    .build();
            rabbitTemplate.convertAndSend("InventarioVentasExchange", "inventario.actualizado-ventas", actualizacionEvent);
        } else if (motivoCodigo.equals("COM")) {
            ComprobantesComprasCaDTO comprobantesComprasCaDTO = (ComprobantesComprasCaDTO) comprobantesCabecera;
            List<ComprobantesComprasPagosEventDTO> comprasCobrosDTO = (List<ComprobantesComprasPagosEventDTO>) cobrosDTO;
            InventarioActualizadoCompraEvent inventarioEvent = InventarioActualizadoCompraEvent.builder()
                    .idAlmacen(idAlmacen)
                    .comprobantesComprasCa(comprobantesComprasCaDTO)
                    .codigoFormaPago(codigoFormaPago)
                    .montoTotal(montoTotal)
                    .comprobantesComprasPagosDTO(comprasCobrosDTO)
                    .build();
            rabbitTemplate.convertAndSend("InventarioCompraExchange", "inventario.actualizado", inventarioEvent);
        }
    }


    private MovimientosCabeceraEntity crearMovimientoCabecera(ComprobanteCabeceraDTO<?> comprobantesCabecera,
                                                              List<ComprobanteDetalleRequest> comprobanteDetalle,
                                                              AlmacenEntity almacen, MovimientosMotivosEntity motivo, BigDecimal montoTotal) {
        Long lastNumber = movimientosCabeceraRepository.findMaxNumber();
        AtomicReference<Integer> cantidadEnvases = new AtomicReference<>(0);
        // Obtener el código del tipo de comprobante
        String tipoDocumentoReferencia = null;
        if (comprobantesCabecera.getComprobantesTipos() instanceof ComprobantesComprasTiposDTO) {
            tipoDocumentoReferencia = ((ComprobantesComprasTiposDTO) comprobantesCabecera.getComprobantesTipos()).getCodigo();
        }

        // Aquí trabajas directamente con la entidad MovimientosCabeceraEntity
        MovimientosCabeceraEntity movimiento = new MovimientosCabeceraEntity();
        movimiento.setIdEmpresa(comprobantesCabecera.getIdEmpresa());
        movimiento.setNumero(lastNumber + 1);
        movimiento.setFechaEmision(LocalDate.now());
        movimiento.setTotal(montoTotal);
        movimiento.setMotivoCodigo(motivo); // Se usa la entidad directamente
        movimiento.setIdUsuario(comprobantesCabecera.getUsuarioCreacion());
        movimiento.setMonedaCodigo(comprobantesCabecera.getCodigoMoneda());
        movimiento.setIdAlmacen(almacen); // Se usa la entidad directamente
        movimiento.setTipoDocumentoReferencia(tipoDocumentoReferencia);
        movimiento.setSerieDocumentoReferencia(comprobantesCabecera.getSerie());
        movimiento.setNumeroDocumentoReferencia(comprobantesCabecera.getNumero());
        movimiento.setObservaciones(comprobantesCabecera.getObservacion());
        movimiento.setIdEntidad(comprobantesCabecera.getIdTercero());
        // Agregar los detalles del movimiento
        List<MovimientosDetallesEntity> detalles = comprobanteDetalle.stream()
                .map(cd -> {
                    cantidadEnvases.getAndSet(cantidadEnvases.get() + cd.getCantidad());
                    ProductosEntity producto = productosRepository.findById(cd.getIdProducto()).orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con ID: " + cd.getIdProducto()));
                    EnvaseEntity envase = envaseRepository.findById(cd.getIdEnvase()).orElseThrow(() -> new EntityNotFoundException("Envase no encontrado con ID: " + cd.getIdEnvase()));
                    MovimientosDetallesEntity detalle = new MovimientosDetallesEntity();
                    detalle.setIdMovimiento(movimiento);
                    detalle.setIdEmpresa(comprobantesCabecera.getIdEmpresa());
                    detalle.setIdProducto(producto);
                    detalle.setEnvase(envase);
                    detalle.setPeso(cd.getPeso());
                    detalle.setTotal(cd.getPrecioUnitario());
                    detalle.setCantidad(cd.getCantidad());
                    detalle.setUsuarioCreacion(comprobantesCabecera.getUsuarioCreacion());
                    return detalle;
                })
                .collect(Collectors.toList());

        movimiento.setMovimientosDetallesEntity(detalles);
        movimiento.setCantidadEnvaces(cantidadEnvases.get());
        movimiento.setFechaIngresoSalida(comprobantesCabecera.getFechaEmision());
        movimiento.setUsuarioCreacion(comprobantesCabecera.getUsuarioCreacion());
        return movimiento;
    }

    private void manejarError(String codigoProductoCompraVenta, Long comprobanteId,String motivoCodigo, String exchange, String routingKey, String source, Exception e, Channel channel, Message message) throws IOException {
        log.error("Error al actualizar el inventario: " + e.getMessage());
        if (motivoCodigo.equals("VEN")) {
            VentaFailedEvent ventaFailed = VentaFailedEvent.builder()
                    .id(comprobanteId)
                    .source(source)
                    .build();
            rabbitTemplate.convertAndSend(exchange, routingKey, ventaFailed);
        }else {
            CompraFailedEvent compraFailed = CompraFailedEvent.builder()
                    .id(comprobanteId)
                    .source(source)
                    .build();
            rabbitTemplate.convertAndSend(exchange, routingKey, compraFailed);
        }
        channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
    }
    private List<ComprobanteDetalleRequest> obtenerDetalleComprobante(ComprobanteCabeceraDTO<?> comprobantesCabecera) {
        return Optional.ofNullable(comprobantesCabecera.obtenerDetalle())
                .orElse(Collections.emptyList())
                .stream()
                .map(cd -> new ComprobanteDetalleRequest(
                        cd.getNumero(),
                        cd.getDescripcion(),
                        cd.getCantidad(),
                        cd.getIdProducto(),
                        cd.getIdEnvase(),
                        cd.getPeso(),
                        cd.getPrecioUnitario(),
                        cd.getDescuento()))
                .collect(Collectors.toList());
    }
    private AlmacenEntity obtenerAlmacen(Long idAlmacen) {
        return almacenRepository.findById(idAlmacen)
                .orElseThrow(() -> new EntityNotFoundException("Almacén no encontrado"));
    }

    private MovimientosMotivosEntity obtenerMotivo(String motivoCodigo) {
        return movimientosMotivosRepository.findById(motivoCodigo)
                .orElseThrow(() -> new EntityNotFoundException("Motivo no encontrado"));
    }

    @RabbitListener(queues = "VentaCompensarQueue")
    public void handleVentaCompensacion(CompensacionCompraEvent event, Channel channel, Message message) throws IOException {
        try {
            if(!event.getSource().equals("inventario")){
                log.info("Compensación en inventario para la venta: " + event.getId());
                Optional<AlmacenEntity> almacen = puntoVentaRepository.findByIdAlmacen(event.getIdPuntoVenta());
                if (almacen.isEmpty()) {
                    throw new RuntimeException("No se encontro el almacen con ID: " + event.getIdPuntoVenta());
                }
                revertirStock(event.getComprobanteDetalleRequest(), almacen.get().getId(), event.getIdEmpresa(), "VEN");
                log.info("Inventario revertido para la venta compensada: " + event.getId());
            } else {
                log.info("Ignorando compensación en Inventario porque la fuente de error es: " + event.getSource());
            }
        } catch (Exception e) {
            log.error("Error al revertir el inventario: " + e.getMessage());
            throw new RuntimeException("Error al revertir el inventario: " + e.getMessage());
        } finally {
            // Confirmar manualmente el mensaje
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }
    }
    @RabbitListener(queues = "CompraCompensarQueue")
    public void handleCompensacionCompra(CompensacionCompraEvent event, Channel channel, Message message) throws IOException {
        try {
            if (!event.getSource().equals("inventario")) {
                log.info("Compensación en Inventario para la compra: " + event.getId());
                try {
                    Optional<AlmacenEntity> almacen = puntoVentaRepository.findByIdAlmacen(event.getIdPuntoVenta());
                    if (almacen.isEmpty()) {
                        throw new RuntimeException("No se encontro el almacen con ID: " + event.getIdPuntoVenta());
                    }
                    revertirStock(event.getComprobanteDetalleRequest(), almacen.get().getId(), event.getIdEmpresa(), "COM");
                    log.info("Inventario revertido para la venta compensada: " + event.getId());
                } catch (Exception e) {
                    log.error("Error al revertir el inventario: " + e.getMessage());
                    throw new RuntimeException("Error al revertir el inventario: " + e.getMessage());
                }
            } else {
                log.info("Ignorando compensación en Inventario porque la fuente de error es: " + event.getSource());
            }
        } catch (Exception e) {
            log.error("Error al revertir el inventario: " + e.getMessage());
            throw new RuntimeException("Error al revertir el inventario: " + e.getMessage());
        } finally {
            // Confirmar manualmente el mensaje
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }
    }
    private BigDecimal calcularMontoTotal(List<ComprobanteDetalleRequest> detalleComprobantes) {
        BigDecimal montoTotal = BigDecimal.ZERO;
        for (ComprobanteDetalleRequest comprobanteDetalle : detalleComprobantes) {
            //monto total x detalle es peso x precio unitario menos descuento
            montoTotal = montoTotal.add(comprobanteDetalle.getPeso().multiply(comprobanteDetalle.getPrecioUnitario()).subtract(comprobanteDetalle.getDescuento()));
        }
        return montoTotal;
    }

    @Transactional
    public void actualizarStock(List<ComprobanteDetalleRequest> comprobanteDetalleRequest, Long idAlmacen, Long idEmpresa, String tipo, String codigoProductoCompraVenta) {
        //ALERTA: se modificara para aactualizar el stock del idProducto de pollo sacrificado que vendra por defecto al genrar la venta, ya no sera asi
        ProductosEntity productoSa = productosRepository.findByCodigo(codigoProductoCompraVenta).orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con Codigo: " + codigoProductoCompraVenta));
        for (ComprobanteDetalleRequest detalle : comprobanteDetalleRequest) {
            StockAlmacen stockAlmacen = productoSa.getStockAlmacenList().stream().filter(stock -> stock.getEnvase() == null).findFirst().orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con ID: " + productoSa.getCodigo()));
            if (stockAlmacen != null) {
                EnvaseEntity envase = envaseRepository.findById(detalle.getIdEnvase()).orElseThrow(() -> new EntityNotFoundException("Envase no encontrada con ID: " + detalle.getIdEnvase()));
                if(tipo.equals("VEN")) {
                    log.info("venta");
                    stockAlmacen.setCantidadProducto(stockAlmacen.getCantidadProducto() - (detalle.getCantidad() * envase.getCapacidad()));
                } else if (tipo.equals("COM")) {
                    log.info("compra");
                    stockAlmacen.setCantidadProducto(stockAlmacen.getCantidadProducto() + (detalle.getCantidad() * envase.getCapacidad()));
                }
                stockAlmacenRepository.save(stockAlmacen);
                log.info("Producto actualizado en el almacén: " + detalle.getIdProducto() + " por " + detalle.getCantidad());
            } else {
                throw new ProductoNotFoundException("El producto no existe en el almacén especificado");
            }
        }
    }

    @Transactional
    public void revertirStock(List<ComprobanteDetalleRequest> comprobanteDetalleRequest, Long idAlmacen, Long idEmpresa, String tipo) {
        if(tipo.equals("VEN")) {//ALERTA: se modificara para aactualizar el stock del idProducto de pollo sacrificado que vendra por defecto al genrar la venta
            ProductosEntity productoSa = productosRepository.findById(comprobanteDetalleRequest.get(0).getIdProducto()).orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con ID: " + comprobanteDetalleRequest.get(0).getIdProducto()));
            for (ComprobanteDetalleRequest detalle : comprobanteDetalleRequest) {
                StockAlmacen stockAlmacen = stockAlmacenRepository.findByProductoCodigoAndEnvaseIsNull(productoSa.getCodigo()).orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con ID: " + productoSa.getCodigo()));
                if (stockAlmacen != null) {
                    EnvaseEntity envase = envaseRepository.findById(detalle.getIdEnvase()).orElseThrow(() -> new EntityNotFoundException("Envase no encontrada con ID: " + detalle.getIdEnvase()));
                    stockAlmacen.setCantidadProducto(stockAlmacen.getCantidadProducto() + (detalle.getCantidad() * envase.getCapacidad()));
                    stockAlmacenRepository.save(stockAlmacen);
                    log.info("Producto actualizado en el almacén: " + detalle.getIdProducto() + " por " + detalle.getCantidad());
                } else {
                    throw new ProductoNotFoundException("El producto no existe en el almacén especificado");
                }
            }
        }
        if(tipo.equals("COM")) {
            ProductosEntity productoSa = productosRepository.findById(comprobanteDetalleRequest.get(0).getIdProducto()).orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con ID: " + comprobanteDetalleRequest.get(0).getIdProducto()));
            for (ComprobanteDetalleRequest detalle : comprobanteDetalleRequest) {
                StockAlmacen stockAlmacen = stockAlmacenRepository.findByProductoCodigoAndEnvaseIsNull(productoSa.getCodigo()).orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con ID: " + productoSa.getCodigo()));
                if (stockAlmacen != null) {
                    EnvaseEntity envase = envaseRepository.findById(detalle.getIdEnvase()).orElseThrow(() -> new EntityNotFoundException("Envase no encontrada con ID: " + detalle.getIdEnvase()));
                    stockAlmacen.setCantidadProducto(stockAlmacen.getCantidadProducto() - (detalle.getCantidad() * envase.getCapacidad()));
                    stockAlmacenRepository.save(stockAlmacen);
                    log.info("Producto actualizado en el almacén: " + detalle.getIdProducto() + " por " + detalle.getCantidad());
                } else {
                    throw new ProductoNotFoundException("El producto no existe en el almacén especificado");
                }
            }
        }
    }
    //OBTENER EL ESTOCK PARA VENTAS
    private Long obtnerStockProductoVenta(String codigo){
        try {
            //log.info("Obteniendo stock de producto: " + codigo);
            Optional<ProductosEntity> productosEntity = productosRepository.findByCodigo(codigo);
            if (productosEntity.isPresent()) {
                //log.info("Encontro producto: " + codigo);
                Optional<StockAlmacen> stockAlmacen = productosEntity.get()
                        .getStockAlmacenList()
                        .stream()
                        .filter(stock -> stock.getEnvase() == null)
                        .findFirst();
                if (stockAlmacen.isPresent()) {
                    //log.info("Encontro stock de producto: " + codigo);
                    return (long) stockAlmacen.get().getCantidadProducto();
                }
            }
            //log.info("No encontro producto: " + codigo);
            return 0L;
        } catch (Exception e) {
            log.error("Error al obtener stock de producto: " + e.getMessage());
            throw new ProductoNotFoundException("Error al obtener stock de producto: " + e.getMessage());
        }
    }
    @Async
    public void executeStream(SseEmitter emitter, String codigo) {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            try {
                Long cantidad = obtnerStockProductoVenta(codigo);
                if (cantidad == null) {
                    throw new RuntimeException("No se encontro el stock de producto: " + codigo);
                }
                //log.info("Cantidad: " + cantidad);
                emitter.send(cantidad);
            } catch (IOException e) {
                log.error("Error en la tarea asíncrona: " + e.getMessage());
                emitter.completeWithError(e);
            }
        }, 0, 5, TimeUnit.SECONDS);
    }



}
