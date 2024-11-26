package com.microservice.ventas.service.compras;

import com.microservice.ventas.client.EmpresaClient;
import com.microservice.ventas.controller.DTO.ComprobanteDetalleRequest;
import com.microservice.ventas.controller.DTO.compras.CompraRequest;
import com.microservice.ventas.controller.DTO.compras.ComprobantesComprasCaDTO;
import com.microservice.ventas.controller.DTO.compras.ComprobantesComprasTiposDTO;
import com.microservice.ventas.entity.*;
import com.microservice.ventas.event.CompensacionCompraEvent;
import com.microservice.ventas.event.CompraCreadaEvent;
import com.microservice.ventas.event.CompraFailedEvent;
import com.microservice.ventas.event.ComprobantesComprasPagosEventDTO;
import com.microservice.ventas.exception.ComprobanteCompraException;
import com.microservice.ventas.repository.IComprobanteCompraCaRepository;
import com.microservice.ventas.repository.IComprobantesCompraEstadoRepository;
import com.microservice.ventas.repository.IComprobantesComprasCuotasRepository;
import com.microservice.ventas.repository.IComprobantesTiposComprasRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import java.util.List;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class ComprasServiceImpl implements IComprasService {
    private final IComprobanteCompraCaRepository iComprobanteCompraCaRepository;
    private final IComprobantesTiposComprasRepository iComprobantesTiposComprasRepository;
    private final IComprobantesComprasCuotasRepository iComprobantesComprasCuotasRepository;
    private final IComprobantesCompraEstadoRepository iComprobantesCompraEstadoRepository;
    private final ModelMapper modelMapper;
    private final RabbitTemplate rabbitTemplate;
    private final CompraEventHandler compraEventHandler;


    @Override
    public CompletableFuture<Long> save(CompraRequest compraRequest) {

        ComprobantesComprasCaDTO comprobantesComprasCaDTO = validateAndPrepareCompra(compraRequest);

        ComprobantesComprasCaEntity savedEntity = saveInitialCompra(compraRequest, comprobantesComprasCaDTO);

        if ("CRE".equals(compraRequest.getCodigoEstado())) {
            saveCuotasCompra(compraRequest, savedEntity);
        }

        return initiateSagaAndWaitCompletionAsync(compraRequest, comprobantesComprasCaDTO, savedEntity)
                .thenApply(compraId -> {
                    log.info("Compra completada con ID: {}", compraId);
                    return compraId;
                })
                .exceptionally(e -> {
                    log.error("Error durante la saga de compra: {}", e.getMessage());
                    throw new RuntimeException("Error al completar la compra", e);
                });

    }
    private ComprobantesComprasCaDTO validateAndPrepareCompra(CompraRequest compraRequest) {
        ComprobantesComprasCaDTO comprobantesComprasCaDTO = compraRequest.getComprobantesComprasCa();
        if (comprobantesComprasCaDTO == null) {
            throw new RuntimeException("No se puede guardar la compra, el comprobante de compra es nulo");
        }
        return comprobantesComprasCaDTO;
    }
    private ComprobantesComprasCaEntity saveInitialCompra(CompraRequest compraRequest,
                                                          ComprobantesComprasCaDTO comprobantesComprasCaDTO) {
        ComprobantesComprasCaEntity comprobantesComprasCaEntity = modelMapper.map(
                comprobantesComprasCaDTO, ComprobantesComprasCaEntity.class);

        ComprobantesTiposComprasEntity tipo = iComprobantesTiposComprasRepository.findByIdEmpresaAndCodigo(compraRequest.getComprobantesComprasCa().getIdEmpresa(),compraRequest.getComprobantesComprasCa().getComprobantesTipos().getCodigo()).orElseThrow(() -> new RuntimeException("No se puede guardar la compra, el tipo de compra es nulo"));
        ComprobantesComprasEstadosEntity estado = iComprobantesCompraEstadoRepository.findByIdEmpresaAndCodigo(compraRequest.getComprobantesComprasCa().getIdEmpresa(),compraRequest.getComprobantesComprasCa().getComprobanteCompraEstados().getCodigo()).orElseThrow(() -> new RuntimeException("No se puede guardar la compra, el estado de compra es nulo"));
        comprobantesComprasCaEntity.setComprobantesTiposEntity(tipo);
        comprobantesComprasCaEntity.setComprobanteCompraEstadosEntity(estado);

        List< ComprobantesComprasDetalleEntity> comprasDetalleList = compraRequest.getComprobantesComprasCa().getComprobantesComprasDetalle().stream()
                .map(cd -> {
                    ComprobantesComprasDetalleEntity comprobantesComprasDetalleEntity = modelMapper.map(cd, ComprobantesComprasDetalleEntity.class);
                    comprobantesComprasDetalleEntity.setComprobantesComprasCaEntity(comprobantesComprasCaEntity);
                    return comprobantesComprasDetalleEntity;
                })
                .collect(Collectors.toList());

        comprobantesComprasCaEntity.setComprobantesComprasDetalleEntity(comprasDetalleList);

        ComprobantesComprasCaEntity savedEntity = iComprobanteCompraCaRepository.saveAndFlush(comprobantesComprasCaEntity);
        return savedEntity;
    }
    private void saveCuotasCompra(CompraRequest compraRequest, ComprobantesComprasCaEntity savedEntity) {
        compraRequest.getComprobantesComprasCa().getComprobantesComprasCuotas()
                .forEach(cuota -> {
                    ComprobantesComprasCuotasEntity comprobantesComprasCuotas = ComprobantesComprasCuotasEntity.builder()
                            .idEmpresa(savedEntity.getIdEmpresa())
                            .comprobanteCabeceraEntity(savedEntity)
                            .nroCuota(cuota.getNroCuota())
                            .fechaVencimiento(cuota.getFechaVencimiento())
                            .importe(cuota.getImporte())
                            .codigoMoneda(savedEntity.getCodigoMoneda())
                            .usuarioCreacion(savedEntity.getUsuarioCreacion())
                            .build();
                    iComprobantesComprasCuotasRepository.save(comprobantesComprasCuotas);
                });
    }
    public CompletableFuture<Long> initiateSagaAndWaitCompletionAsync(
            CompraRequest compraRequest,
            ComprobantesComprasCaDTO comprobantesComprasCaDTO,
            ComprobantesComprasCaEntity savedEntity) {
        // Actualizar el id del comprobante de compra
        comprobantesComprasCaDTO.setId(savedEntity.getId());
        List<ComprobantesComprasPagosEventDTO> comprobantesComprasPagosDTO = compraRequest.getComprobantesComprasPagos();

        comprobantesComprasPagosDTO.forEach(cd -> cd.setIdComprobanteCompra(savedEntity.getId()));

        CompraCreadaEvent event = CompraCreadaEvent.builder()
                .codigoFormaPago(compraRequest.getCodigoEstado())
                .comprobantesComprasCa(comprobantesComprasCaDTO)
                .idAlmacen(compraRequest.getIdAlmacen())
                .generarMovimiento(compraRequest.getGenerarMovimiento())
                .comprobantesComprasPagosDTO(comprobantesComprasPagosDTO)
                .codigoProductoCompra(compraRequest.getCodigoProductoCompra())
                .build();

        CompletableFuture<Boolean> sagaResult = compraEventHandler.registerSagaCompletion(savedEntity.getId());
        rabbitTemplate.convertAndSend("CompraExchange", "compra.creada", event);

        return sagaResult.thenApply(success -> {
            if (success) {
                ComprobantesComprasCaEntity finalEntity = iComprobanteCompraCaRepository
                        .findById(savedEntity.getId())
                        .orElseThrow(() -> new RuntimeException("Compra no encontrada después de la saga"));

                if ("SUCCESS".equals(finalEntity.getEstadoCreacion())) {
                    return finalEntity.getId();
                } else if ("FALLO_PAGO".equals(finalEntity.getEstadoCreacion())) {
                    emitirEventoCompensacion(finalEntity.getId(), "finanzas", compraRequest.getCodigoProductoCompra());
                    throw new RuntimeException("Pago fallido en finanzas, la compra no puede completarse");
                } else if ("FALLO_STOCK".equals(finalEntity.getEstadoCreacion())) {
                    throw new RuntimeException("Error en el inventario, la compra no puede completarse");
                }
            }
            throw new RuntimeException("Saga completada sin confirmación de éxito");
        });
    }

    @Transactional
    private void emitirEventoCompensacion(Long compraId, String source, String codigoProductoCompra) {
        try {
            ComprobantesComprasCaEntity comprobantesComprasCa = iComprobanteCompraCaRepository.findById(compraId).orElseThrow(() -> new RuntimeException("Comprobante de compra no encontrado"));
            List<ComprobanteDetalleRequest> comprobanteDetalle = comprobantesComprasCa.getComprobantesComprasDetalleEntity().stream()
                    .map(cd -> new ComprobanteDetalleRequest(cd.getCantidad(),cd.getIdProducto(),cd.getIdEnvase(),cd.getPeso(),cd.getPrecioUnitario(),cd.getDescuento()))
                    .collect(Collectors.toList());

            CompensacionCompraEvent compensacionEvent = CompensacionCompraEvent.builder()
                    .id(compraId)
                    .comprobanteDetalleRequest(comprobanteDetalle)
                    .idPuntoVenta(comprobantesComprasCa.getIdPuntoVenta())
                    .idEmpresa(comprobantesComprasCa.getIdEmpresa())
                    .source(source)
                    .codigoProductoCompra(codigoProductoCompra)
                    .serie(comprobantesComprasCa.getSerie())
                    .numero(comprobantesComprasCa.getNumero())
                    .usuarioCreacion(comprobantesComprasCa.getUsuarioCreacion())
                    .codigoMoneda(comprobantesComprasCa.getCodigoMoneda())
                    .observacion(comprobantesComprasCa.getObservacion())
                    .idCliente(comprobantesComprasCa.getIdProveedor())
                    .fechaEmision(comprobantesComprasCa.getFechaEmision())
                    .build();
            log.info("Compensación iniciada para la compra: " + compraId + " debido a fallo en " + source);
            log.info("Recibido evento de compensacion de compra");

            iComprobanteCompraCaRepository.deleteById(compraId);
            rabbitTemplate.convertAndSend("CompensacionCompraExchange", "compra.compensar", compensacionEvent);
        } catch (Exception e) {
            log.error("Error al eliminar el comprobante de compra en la compensacion: " + e.getMessage());
            throw new ComprobanteCompraException("Error al eliminar el comprobante de compra en la compensacion: " + e.getMessage());
        }
    }

    @Override
    public Boolean deleteById(Long id) {
        return null;
    }

    @Override
    public Boolean anularCompra(Long id) {
        return null;
    }

    @Override
    public List<ComprobantesComprasTiposDTO> getComprobantesTiposCompras(Long IdEmpresa) {
        try {
            List<ComprobantesTiposComprasEntity> comprobantesComprasTipos = iComprobantesTiposComprasRepository.findByIdEmpresa(IdEmpresa);
            return comprobantesComprasTipos.stream().map(comprobantesComprasTiposEntity -> modelMapper.map(comprobantesComprasTiposEntity, ComprobantesComprasTiposDTO.class)).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error al obtener los tipos de compra: " + e.getMessage());
            throw new RuntimeException("Error al obtener los tipos de compra: " + e.getMessage());
        }
    }
}
