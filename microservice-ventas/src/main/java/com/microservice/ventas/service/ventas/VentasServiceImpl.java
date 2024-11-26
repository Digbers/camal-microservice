package com.microservice.ventas.service.ventas;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.ventas.client.EmpresaClient;
import com.microservice.ventas.controller.DTO.*;
import com.microservice.ventas.controller.DTO.ventas.ComprobantesTiposVentasDTO;
import com.microservice.ventas.controller.DTO.ventas.ComprobantesVentasCabDTO;
import com.microservice.ventas.controller.DTO.ventas.VentaRequest;
import com.microservice.ventas.entity.*;
import com.microservice.ventas.event.*;
import com.microservice.ventas.exception.ComprobanteCompraException;
import com.microservice.ventas.exception.ComprobanteVentaException;
import com.microservice.ventas.repository.*;
import com.microservice.ventas.service.ReportService;
import com.rabbitmq.client.Channel;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VentasServiceImpl implements IVentasService {
    private final IcomprobantesVentasCabRepository iComprobantesVentasCabRepository;
    private final IComprobantesVentasEstadosRepository iComprobantesVentasEstadosRepository;
    private final ModelMapper modelMapper;
    private final EmpresaClient empresaClient;
    private final RabbitTemplate rabbitTemplate;
    private final IComprobantesTiposVentasRepository iComprobantesTiposVentasRepository;
    private final ISeriesRepository iSeriesRepository;
    private final IComprobantesVentasCuotasRepository iComprobantesVentasCuotasRepository;
    private final ReportService reportService;
    private final VentaEventHandler ventaEventHandler;

    @Override
    public CompletableFuture<Long> save(VentaRequest ventaRequest) {
        log.info("Guardando venta");
        // 1. Validar y preparar la venta
        ComprobantesVentasCabDTO comprobantesVentasCabDTO = validateAndPrepareVenta(ventaRequest);
        // 2. Guardar la venta en estado PENDIENTE
        ComprobantesVentasCabEntity savedEntity = saveInitialVenta(ventaRequest, comprobantesVentasCabDTO);
        // 3. Si es venta a crédito, guardar cuotas
        if ("CRE".equals(ventaRequest.getCodigoEstado())) {
            saveCuotasVenta(ventaRequest, savedEntity);
        }
        // 4. Iniciar la saga de forma asíncrona y devolver el ID de la venta cuando esté listo
        return initiateSagaAndWaitCompletionAsync(ventaRequest, comprobantesVentasCabDTO, savedEntity)
                .thenApply(ventaId -> {
                    log.info("Venta completada con ID: {}", ventaId);
                    return ventaId;
                })
                .exceptionally(e -> {
                    log.error("Error durante la saga de venta: {}", e.getMessage());
                    throw new RuntimeException("Error al completar la venta", e);
                });
    }

    private ComprobantesVentasCabDTO validateAndPrepareVenta(VentaRequest ventaRequest) {
        ComprobantesVentasCabDTO comprobantesVentasCabDTO = ventaRequest.getComprobantesVentasCabDTO();
        if (comprobantesVentasCabDTO == null) {
            throw new RuntimeException("No se puede guardar la venta, el comprobante de venta es nulo");
        }
        return comprobantesVentasCabDTO;
    }
    private ComprobantesVentasCabEntity saveInitialVenta(VentaRequest ventaRequest,
                                                         ComprobantesVentasCabDTO comprobantesVentasCabDTO) {
        ComprobantesVentasCabEntity comprobantesVentasCabEntity = modelMapper.map(
                comprobantesVentasCabDTO, ComprobantesVentasCabEntity.class);
        ComprobantesTiposVentasEntity tipo = iComprobantesTiposVentasRepository.findByIdEmpresaAndCodigo(comprobantesVentasCabEntity.getIdEmpresa(), comprobantesVentasCabDTO.getComprobantesTipos().getCodigo()).orElseThrow(() -> new RuntimeException("Tipo de comprobante no encontrado"));
        ComprobantesVentasEstadoEntity estado =  iComprobantesVentasEstadosRepository.findByIdEmpresaAndCodigo(comprobantesVentasCabEntity.getIdEmpresa(), comprobantesVentasCabDTO.getComprobantesVentaEstado().getCodigo()).orElseThrow(() -> new RuntimeException("Estado de venta no encontrado"));
        comprobantesVentasCabEntity.setComprobantesTiposEntity(tipo);
        comprobantesVentasCabEntity.setComprobantesVentasEstadoEntity(estado);
        String maxNumero = iComprobantesVentasCabRepository.findMaxNumero(comprobantesVentasCabDTO.getSerie(), comprobantesVentasCabDTO.getIdPuntoVenta());
        // Si hay un número máximo en la base de datos, calcular el siguiente número
        if (maxNumero != null) {
            int nuevoNumero = Integer.parseInt(maxNumero) + 1;

            if (!comprobantesVentasCabDTO.getNumero().equals(String.valueOf(nuevoNumero))) {
                comprobantesVentasCabEntity.setNumero(String.valueOf(nuevoNumero));
                log.info("Número actualizado automáticamente a: " + nuevoNumero);
            }
        } else {
            comprobantesVentasCabEntity.setNumero("1");
            log.info("Número inicial asignado: 1");
        }
        // Inicializar estado de la saga
        comprobantesVentasCabEntity.setEstadoCreacion("INICIADO");

        List<ComprobantesVentasDetEntity> detalles = comprobantesVentasCabDTO.getComprobantesVentaDet()
                .stream()
                .map(det -> {
                    ComprobantesVentasDetEntity detEntity = modelMapper.map(det, ComprobantesVentasDetEntity.class);
                    detEntity.setComprobanteCabeceraEntity(comprobantesVentasCabEntity);
                    return detEntity;
                })
                .collect(Collectors.toList());

        comprobantesVentasCabEntity.setComprobantesVentasDetEntity(detalles);
        return iComprobantesVentasCabRepository.saveAndFlush(comprobantesVentasCabEntity);
    }
    private void saveCuotasVenta(VentaRequest ventaRequest, ComprobantesVentasCabEntity savedEntity) {
        ventaRequest.getComprobantesVentasCabDTO().getComprobantesVentasCuotas()
                .forEach(cuota -> {
                    ComprobantesVentasCuotasEntity cuotaEntity = ComprobantesVentasCuotasEntity.builder()
                            .idEmpresa(savedEntity.getIdEmpresa())
                            .comprobanteCabeceraEntity(savedEntity)
                            .nroCuota(cuota.getNroCuota())
                            .fechaVencimiento(cuota.getFechaVencimiento())
                            .importe(cuota.getImporte())
                            .codigoMoneda(savedEntity.getCodigoMoneda())
                            .usuarioCreacion(savedEntity.getUsuarioCreacion())
                            .build();
                    iComprobantesVentasCuotasRepository.save(cuotaEntity);
                });
    }

    public CompletableFuture<Long> initiateSagaAndWaitCompletionAsync(
            VentaRequest ventaRequest,
            ComprobantesVentasCabDTO comprobantesVentasCabDTO,
            ComprobantesVentasCabEntity savedEntity) {

        comprobantesVentasCabDTO.setId(savedEntity.getId());
        List<ComprobantesVentasCobrosEventDTO> comprobantesVentasCobrosDTO = ventaRequest.getComprobantesVentasCobrosDTO();
        comprobantesVentasCobrosDTO.forEach(cd -> cd.setIdComprobanteVenta(savedEntity.getId()));
        VentaCreadaEvent event = VentaCreadaEvent.builder()
                .comprobantesVentasCab(comprobantesVentasCabDTO)
                .idAlmacen(ventaRequest.getIdAlmacen())
                .codigoFormaPago(ventaRequest.getCodigoEstado())
                .comprobantesVentasCobrosDTO(comprobantesVentasCobrosDTO)
                .codigoProductoVenta(ventaRequest.getCodigoProductoVenta())
                .build();

        CompletableFuture<Boolean> sagaResult = ventaEventHandler.registerSagaCompletion(savedEntity.getId());
        rabbitTemplate.convertAndSend("VentasExchange", "venta.creada", event);

        // Devolver un CompletableFuture que se completará cuando el resultado esté listo
        return sagaResult.thenApply(success -> {
            if (success) {
                ComprobantesVentasCabEntity finalEntity = iComprobantesVentasCabRepository
                        .findById(savedEntity.getId())
                        .orElseThrow(() -> new RuntimeException("Venta no encontrada después de la saga"));

                if ("SUCCESS".equals(finalEntity.getEstadoCreacion())) {
                    return finalEntity.getId();
                } else if ("FALLO_PAGO".equals(finalEntity.getEstadoCreacion())) {
                    try {
                        emitirEventoCompensacion(finalEntity.getId(), "finanzas", ventaRequest.getCodigoProductoVenta());
                    } catch (IOException e) {
                        throw new RuntimeException("Error al emitir evento de compensacion" + e.getMessage());
                    }
                    throw new RuntimeException("Pago fallido en finanzas, la venta no puede completarse");
                } else if ("FALLO_STOCK".equals(finalEntity.getEstadoCreacion())) {
                    throw new RuntimeException("Error en el inventario, la venta no puede completarse");
                }
            }
            throw new RuntimeException("Saga completada sin confirmación de éxito");
        });
    }


    @Transactional
        private void emitirEventoCompensacion(Long ventaId, String source, String codigoProductoVenta) throws IOException {
        try {
            ComprobantesVentasCabEntity comprobanteVentaCa = iComprobantesVentasCabRepository.findById(ventaId).orElseThrow(() -> new RuntimeException("Comprobante de venta no encontrado con el id: " + ventaId));
            List<ComprobanteDetalleRequest> comprobanteDetalle = comprobanteVentaCa.getComprobantesVentasDetEntity().stream()
                    .map(cd -> new ComprobanteDetalleRequest(cd.getCantidad(),cd.getIdProducto(),cd.getIdEnvase(),cd.getPeso(),cd.getPrecioUnitario(),cd.getDescuento()))
                    .collect(Collectors.toList());
            CompensacionVentaEvent compensacionEvent = CompensacionVentaEvent.builder()
                    .ventaId(ventaId)
                    .comprobanteDetalleRequest(comprobanteDetalle)
                    .idPuntoVenta(comprobanteVentaCa.getIdPuntoVenta())
                    .idEmpresa(comprobanteVentaCa.getIdEmpresa())
                    .source(source)
                    .codigoProductoVenta(codigoProductoVenta)
                    .serie(comprobanteVentaCa.getSerie())
                    .numero(comprobanteVentaCa.getNumero())
                    .usuarioCreacion(comprobanteVentaCa.getUsuarioCreacion())
                    .codigoMoneda(comprobanteVentaCa.getCodigoMoneda())
                    .observacion(comprobanteVentaCa.getObservacion())
                    .idCliente(comprobanteVentaCa.getIdCliente())
                    .fechaEmision(comprobanteVentaCa.getFechaEmision())
                    .build();
            log.info("Compensación iniciada para la venta: " + ventaId + " debido a fallo en " + source);
            log.info("Recibido evento de compensacion de venta");
            ComprobantesVentasEstadoEntity estado = iComprobantesVentasEstadosRepository.findByIdEmpresaAndCodigo(comprobanteVentaCa.getIdEmpresa(), "ANU").orElseThrow(() -> new RuntimeException("Estado de venta no encontrado"));
            comprobanteVentaCa.setComprobantesVentasEstadoEntity(estado);
            iComprobantesVentasCabRepository.save(comprobanteVentaCa);
            rabbitTemplate.convertAndSend("CompensacionVentasExchange", "venta.compensar", compensacionEvent);
        } catch (Exception e) {
            log.error("Error al eliminar el comprobante de venta en la compensacion: " + e.getMessage());
            throw new ComprobanteCompraException("Error al eliminar el comprobante de venta en la compensacion: " + e.getMessage());
        }
    }


    @Override
    public Boolean deleteById(Long id) {
        try {
            iComprobantesVentasCabRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            throw new ComprobanteVentaException("Error al eliminar la venta: " + e.getMessage());
        }
    }
    @Override
    public Boolean anularVenta(Long id) {
        try {
            ComprobantesVentasCabEntity entity = iComprobantesVentasCabRepository.findById(id)
                    .orElseThrow(() -> new ComprobanteVentaException("Venta no encontrada con ID: " + id));

            if ("ANU".equals(entity.getComprobantesVentasEstadoEntity().getCodigo())) {
                log.info("Venta ya anulada con ID: " + id);
                return true;
            }
            Optional<ComprobantesVentasEstadoEntity> estado = iComprobantesVentasEstadosRepository.findByIdEmpresaAndCodigo(entity.getIdEmpresa(), "ANU");
            if(estado.isEmpty()) {
                System.out.println("No se encontro el estado ANU");
                return false;
            }
            entity.setComprobantesVentasEstadoEntity(estado.get());
            iComprobantesVentasCabRepository.save(entity);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getNumeroXSerie(String serie, Long idPuntoVenta) {
        try {
            //log.info("Ejecutando consulta con serie: " + serie + ", idPuntoVenta: " + idPuntoVenta);
            String serieMax = iComprobantesVentasCabRepository.findMaxNumero(serie, idPuntoVenta);
            //log.info("Resultado de la consulta: " + serieMax);

            if (serieMax == null || serieMax.equals("")) {
                serieMax = "1";
            }
            return serieMax;
        } catch (Exception e) {
            log.error("Error al ejecutar la consulta: " + e.getMessage());
            throw new RuntimeException("Error al obtener el numero de serie: " + e.getMessage());
        }
    }
    @Async
    public void executeStream(SseEmitter emitter, String serie, Long idPuntoVenta) {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            try {
                String numero = iComprobantesVentasCabRepository.findMaxNumero(serie, idPuntoVenta);
                if (numero == null || numero.equals("")) {
                    numero = "1";
                }
                //log.info("Numero: " + numero);
                emitter.send(numero);
            } catch (IOException e) {
                log.error("Error en la tarea asíncrona: " + e.getMessage());
                emitter.completeWithError(e);
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    @Override
    public List<ComprobantesTiposVentasDTO> getComprobantesTiposVentas(Long idEmpresa) {
        try {
            List<ComprobantesTiposVentasDTO> comprobantesTiposVentas = iComprobantesTiposVentasRepository.findAllByIdEmpresa(idEmpresa).stream().map(comprobantesTiposVentasEntity -> modelMapper.map(comprobantesTiposVentasEntity, ComprobantesTiposVentasDTO.class)).collect(Collectors.toList());
            return comprobantesTiposVentas;
        } catch (Exception e) {
            throw new ComprobanteVentaException("Error al obtener los tipos de comprobantes de venta: " + e.getMessage());
        }
    }

    @Override
    public List<SeriesDTO> getSeries(String codigoTipoComprobante, Long idPuntoVenta) {
        try {
            List<SeriesDTO> series = iSeriesRepository.findByCodigoTipoComprobanteCodigo(codigoTipoComprobante, idPuntoVenta).stream().map(seriesEntity -> modelMapper.map(seriesEntity, SeriesDTO.class)).collect(Collectors.toList());
            return series;
        } catch (Exception e) {
            throw new ComprobanteVentaException("Error al obtener las series de comprobantes de venta: " + e.getMessage());
        }
    }

    @Override
    public String getComprobante(Long idComprobante) {
        try {
            return reportService.generarReporteBase64(idComprobante);
        } catch (Exception e) {
            log.error("Error al obtener el comprobante: " + e.getMessage());
            throw new RuntimeException("Error al obtener el comprobante: " + e.getMessage());
        }
    }
    public String getEmpresa(Long idempresa) {
        try {
            EmpresaDTO empresa = empresaClient.obtenerDetallesEmpresa(idempresa);
            return empresa.getRazonSocial();
        } catch (Exception e) {
            log.error("Error al obtener el comprobante: " + e.getMessage());
            throw new RuntimeException("Error al obtener el comprobante: " + e.getMessage());
        }
    }
}

