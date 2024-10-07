package com.microservice.ventas.service.ventas;

import com.microservice.ventas.client.EmpresaClient;
import com.microservice.ventas.controller.DTO.*;
import com.microservice.ventas.controller.DTO.ventas.ComprobantesTiposVentasDTO;
import com.microservice.ventas.controller.DTO.ventas.ComprobantesVentasCabDTO;
import com.microservice.ventas.controller.DTO.ventas.VentaRequest;
import com.microservice.ventas.entity.ComprobantesVentasCabEntity;
import com.microservice.ventas.entity.ComprobantesVentasCuotasEntity;
import com.microservice.ventas.entity.ComprobantesVentasEstadoEntity;
import com.microservice.ventas.event.CompensacionCompraEvent;
import com.microservice.ventas.event.CompensacionVentaEvent;
import com.microservice.ventas.event.CompraFailedEvent;
import com.microservice.ventas.event.VentaCreadaEvent;
import com.microservice.ventas.exception.ComprobanteCompraException;
import com.microservice.ventas.exception.ComprobanteVentaException;
import com.microservice.ventas.exception.EmpresaNoEncontradaException;
import com.microservice.ventas.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Transactional
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

    @Override
    public ComprobantesVentasCabDTO save(VentaRequest ventaRequest) {
        ComprobantesVentasCabDTO comprobantesVentasCabDTO = ventaRequest.getComprobantesVentasCabDTO();
        boolean exist = empresaClient.verificarEmpresaExiste(comprobantesVentasCabDTO.getIdEmpresa());
        if (!exist) {
            throw new EmpresaNoEncontradaException("La empresa con ID " + comprobantesVentasCabDTO.getIdEmpresa() + " no existe.");
        }
        try {
            ComprobantesVentasCabEntity comprobantesVentasCabEntity = modelMapper.map(comprobantesVentasCabDTO, ComprobantesVentasCabEntity.class);
            ComprobantesVentasCabEntity savedEntity = iComprobantesVentasCabRepository.save(comprobantesVentasCabEntity);

            if(ventaRequest.getCodigoFormaCobro().equals("CRE")){
                ventaRequest.getCuotasRequest().forEach(cuota -> {
                    ComprobantesVentasCuotasEntity comprobantesVentasCuotas = ComprobantesVentasCuotasEntity.builder()
                            .idEmpresa(savedEntity.getIdEmpresa())
                            .comprobanteCabeceraEntity(savedEntity)
                            .nroCuota(cuota.getNumeroCuota())
                            .fechaVencimiento(cuota.getFechaVencimiento())
                            .importe(cuota.getImporteTotal())
                            .codigoMoneda(savedEntity.getCodigoMoneda())
                            .usuarioCreacion(savedEntity.getUsuarioCreacion())
                            .build();
                    iComprobantesVentasCuotasRepository.save(comprobantesVentasCuotas);
                });
            }
            // Publicar evento de venta creada
            VentaCreadaEvent event = VentaCreadaEvent.builder()
                    .comprobantesVentasCab(comprobantesVentasCabDTO)
                    .idAlmacen(ventaRequest.getIdAlmacen())
                    .codigoFormaPago(ventaRequest.getCodigoFormaCobro())
                    .build();
            rabbitTemplate.convertAndSend("VentasExchange", "venta.creada", event);

            return modelMapper.map(savedEntity, ComprobantesVentasCabDTO.class);
        } catch (Exception e) {
            throw new ComprobanteVentaException("Error al guardar el comprobante de venta: " + e.getMessage());
        }
    }
    @RabbitListener(queues = "FinanzasCobrosErrorQueue")
    public void handleFinanzasPagoFallido(CompraFailedEvent event) {
        log.error("Pago fallido en finanzas para la venta: " + event.getId() + " del source: " + event.getSource());
        emitirEventoCompensacion(event.getId(), event.getSource());
    }
    @Transactional
    private void emitirEventoCompensacion(Long ventaId, String source) {
        try {
            ComprobantesVentasCabEntity comprobanteVentaCa = iComprobantesVentasCabRepository.findById(ventaId).orElseThrow(() -> new RuntimeException("Comprobante de ventano encontrado"));
            List<ComprobanteDetalleRequest> comprobanteDetalle = comprobanteVentaCa.getComprobantesVentasDetEntity().stream()
                    .map(cd -> new ComprobanteDetalleRequest(cd.getCantidad(),cd.getIdProducto(),cd.getIdEnvase(),cd.getPeso(),cd.getPrecioUnitario(),cd.getDescuento()))
                    .collect(Collectors.toList());
            CompensacionVentaEvent compensacionEvent = CompensacionVentaEvent.builder()
                    .ventaId(ventaId)
                    .comprobanteDetalleRequest(comprobanteDetalle)
                    .idPuntoVenta(comprobanteVentaCa.getIdPuntoVenta())
                    .idEmpresa(comprobanteVentaCa.getIdEmpresa())
                    .source(source)
                    .build();
            log.info("Compensación iniciada para la venta: " + ventaId + " debido a fallo en " + source);
            log.info("Recibido evento de compensacion de venta");
            // FALTA REVISAR QUE S ENVIA PARA LA COMPENSACION DE ESTA VENTA QEUE VIENE DE UN ERRROR EN FINANZAS
            iComprobantesVentasCabRepository.deleteById(ventaId);
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
            Optional<ComprobantesVentasEstadoEntity> estado = iComprobantesVentasEstadosRepository.findById("ANU");
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
            log.info("Ejecutando consulta con serie: " + serie + ", idPuntoVenta: " + idPuntoVenta);
            String serieMax = iComprobantesVentasCabRepository.findMaxNumero(serie, idPuntoVenta);
            log.info("Resultado de la consulta: " + serieMax);

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
                log.info("Numero: " + numero);
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
}

