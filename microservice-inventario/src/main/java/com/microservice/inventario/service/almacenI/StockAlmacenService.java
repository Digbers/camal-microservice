package com.microservice.inventario.service.almacenI;

import com.microservice.inventario.controller.DTO.MovimientosCabeceraDTO;
import com.microservice.inventario.controller.DTO.request.ConvertirProductoRequest;
import com.microservice.inventario.persistence.entity.*;
import com.microservice.inventario.persistence.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockAlmacenService implements IStockAlmacenService {

    private final IStockAlmacenRepository stockAlmacenRepository;
    private final ProductosRepository productoRepository;
    private final IAlmacenRepository almacenRepository;
    private final EnvaseRepository envaseRepository;
    private final IMovimientoSacrificioCabeceraRepository movimientoSacrificioCabeceraRepository;
    private final IMovimientosCabeceraRepository movimientosCabeceraRepository;
    private final IMovimientosMotivosRepository movimientosMotivosRepository;
    private final ModelMapper modelMapper;


    /**
     * Método para agregar o actualizar un producto en el stock del almacén.
     *
     * @param idAlmacen     ID del almacén
     * @param idProducto    ID del producto
     * @param idEnvase      ID del envase (puede ser opcional)
     * @param cantidadEnvase Cantidad de envases
     * @param cantidadProducto Cantidad de productos
     * @param pesoTotal     Peso total de los productos
     * @return El objeto de StockAlmacen actualizado o creado
     */
    @Override
    @Transactional
    public StockAlmacen registrarProductoEnAlmacen(Long idEmpresa, Long idAlmacen, Long idProducto, Long idEnvase,
                                                   int cantidadEnvase, int cantidadProducto, double pesoTotal) {

        // Buscar las instancias de Producto, Envase y Almacen
        ProductosEntity producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));
        AlmacenEntity almacen = almacenRepository.findById(idAlmacen)
                .orElseThrow(() -> new EntityNotFoundException("Almacén no encontrado"));
        EnvaseEntity envase = null;
        if (idEnvase != null) {
            envase = envaseRepository.findById(idEnvase)
                    .orElseThrow(() -> new EntityNotFoundException("Envase no encontrado"));
        }
        // Verificar si ya existe un registro de StockAlmacen para este producto y almacén
        Optional<StockAlmacen> stockExistente = stockAlmacenRepository.findByIds(idAlmacen, idProducto, idEnvase, idEmpresa);
        StockAlmacen stockAlmacen;
        if (stockExistente.isPresent()) {
            // Si el stock ya existe, actualizar cantidades y peso
            stockAlmacen = stockExistente.get();
            stockAlmacen.setCantidadEnvase(stockAlmacen.getCantidadEnvase() + cantidadEnvase);
            stockAlmacen.setCantidadProducto(stockAlmacen.getCantidadProducto() + cantidadProducto);
            stockAlmacen.setPesoTotal(stockAlmacen.getPesoTotal().add(BigDecimal.valueOf(pesoTotal)));
        } else {
            // Si no existe, crear un nuevo registro
            stockAlmacen = StockAlmacen.builder()
                    .producto(producto)
                    .almacen(almacen)
                    .envase(envase)
                    .cantidadEnvase(cantidadEnvase)
                    .cantidadProducto(cantidadProducto)
                    .pesoTotal(BigDecimal.valueOf(pesoTotal))
                    .fechaRegistro(LocalDate.now())
                    .build();
        }
        // Guardar el registro en el repositorio de StockAlmacen
        return stockAlmacenRepository.save(stockAlmacen);
    }

    @Override
    public Boolean convertirProducto(ConvertirProductoRequest request) {
        // 1. Buscar el producto en StockAlmacen para el almacén especificado (verificación realizada en el aspecto)
        StockAlmacen stock = stockAlmacenRepository.findByIds(request.idAlmacen(), request.idProducto(), request.idEnvase(), request.idEmpresa()).get();

        // 2. Restar la cantidad del producto original
        stock.setCantidadProducto(stock.getCantidadProducto() - request.cantidadProducto());
        stockAlmacenRepository.save(stock);

        // 3. Buscar o crear el StockAlmacen del producto transformado
        Optional<StockAlmacen> stockTransformadoOpt = stockAlmacenRepository.findByIdsWithOptionalEnvase(request.idAlmacen(), request.idProductoTransformasdo(), request.idEnvase(), request.idEmpresa());

        StockAlmacen stockTransformado;
        ProductosEntity productoV = productoRepository.findById(request.idProducto())
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));
        ProductosEntity productoT = productoRepository.findById(request.idProductoTransformasdo())
                .orElseThrow(() -> new EntityNotFoundException("Producto transformado no encontrado"));
        AlmacenEntity almacenT = almacenRepository.findById(request.idAlmacen())
                .orElseThrow(() -> new EntityNotFoundException("Almacén no encontrado"));


        if (stockTransformadoOpt.isPresent()) {
            stockTransformado = stockTransformadoOpt.get();
        } else {
            stockTransformado = new StockAlmacen();
            stockTransformado.setProducto(productoT);
            stockTransformado.setAlmacen(almacenT);
            stockTransformado.setCantidadEnvase(0);
            stockTransformado.setCantidadProducto(0);
        }

        // 4. Sumar la cantidad al producto transformado
        stockTransformado.setCantidadProducto(stockTransformado.getCantidadProducto() + request.cantidadProducto());
        stockAlmacenRepository.save(stockTransformado);

        // 5. Registrar el movimiento en MovimientoSacrificioCabecera y Detalle
        MovimientoSacrificioCabecera movimientoCabecera = MovimientoSacrificioCabecera.builder()
                .idEmpresa(request.idEmpresa())
                .fechaMovimiento(LocalDate.now())
                .responsable(request.usuCodigo())
                .cantidadTotalPollosSacrificados(request.cantidadProducto())
                .observaciones(request.observaciones())
                .usuarioCreacion(request.usuCodigo())
                .build();

        MovimientoSacrificioDetalle movimientoDetalle = MovimientoSacrificioDetalle.builder()
                .movimientoSacrificioCabecera(movimientoCabecera)
                .productoVivo(productoV)
                .productoProcesado(productoT)//no aplica envase en la transformación
                .cantidadPollosSacrificados(request.cantidadProducto())
                .pesoTotal(BigDecimal.valueOf(request.pesoTotal()))// total del producto transformado no individual
                .usuarioCreacion(request.usuCodigo())
                .build();

        movimientoCabecera.setDetalles(List.of(movimientoDetalle));
        movimientoSacrificioCabeceraRepository.save(movimientoCabecera);

        return true;
    }
    @Transactional
    public boolean crearMovimiento(MovimientosCabeceraEntity movimiento) {
        try {
            MovimientosMotivosEntity movimientoMotivo = movimientosMotivosRepository.findById(movimiento.getMotivoCodigo().getCodigo())
                    .orElseThrow(() -> new EntityNotFoundException("Motivo no encontrado"));
            // Obtener la entidad Almacen desde el repositorio
            AlmacenEntity almacenEntity = almacenRepository.findById(movimiento.getIdAlmacen().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Almacén no encontrado"));
            // Guardar el movimiento
            movimiento.setMotivoCodigo(movimientoMotivo);
            movimiento.setIdAlmacen(almacenEntity);
            // Guardar el movimiento en la base de datos
            movimientosCabeceraRepository.save(movimiento);

            log.info("Movimiento creado para la venta: " + movimiento.getNumero());
            return true; // Devuelves true si el movimiento se creó correctamente
        } catch (Exception e) {
            log.error("Error al crear movimiento", e);
            return false; // Devuelves false si hubo un error
        }
    }

}