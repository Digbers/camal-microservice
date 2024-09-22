package com.microservice.ventas.service.ventas;

import com.microservice.ventas.client.EmpresaClient;
import com.microservice.ventas.controller.DTO.*;
import com.microservice.ventas.entity.ComprobantesVentasCabEntity;
import com.microservice.ventas.entity.ComprobantesVentasEstadoEntity;
import com.microservice.ventas.event.VentaCompensarEvent;
import com.microservice.ventas.event.VentaCreadaEvent;
import com.microservice.ventas.exception.ComprobanteVentaException;
import com.microservice.ventas.exception.EmpresaNoEncontradaException;
import com.microservice.ventas.repository.IComprobantesTiposVentasRepository;
import com.microservice.ventas.repository.IComprobantesVentasEstadosRepository;
import com.microservice.ventas.repository.ISeriesRepository;
import com.microservice.ventas.repository.IcomprobantesVentasCabRepository;
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Transactional
public class VentasServiceImpl implements IVentasService {
    @Autowired
    private IcomprobantesVentasCabRepository iComprobantesVentasCabRepository;
    @Autowired
    private IComprobantesVentasEstadosRepository iComprobantesVentasEstadosRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private EmpresaClient empresaClient;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private IComprobantesTiposVentasRepository iComprobantesTiposVentasRepository;
    @Autowired
    private ISeriesRepository iSeriesRepository;

    @Override
    public ComprobantesVentasCabDTO save(VentaRequest ventaRequest) {
        List<ComprobanteDetalleRequest> comprobanteDetalleRequests =new ArrayList<>();
        ComprobantesVentasCabDTO comprobantesVentasCabDTO = ventaRequest.getComprobantesVentasCabDTO();
        FormasDeCobrosRequest formasDeCobrosRequest = ventaRequest.getFormasDeCobrosRequest();
        boolean exist = empresaClient.verificarEmpresaExiste(comprobantesVentasCabDTO.getIdEmpresa());
        if (!exist) {
            throw new EmpresaNoEncontradaException("La empresa con ID " + comprobantesVentasCabDTO.getIdEmpresa() + " no existe.");
        }
        try {
            ComprobantesVentasCabEntity comprobantesVentasCabEntity = modelMapper.map(comprobantesVentasCabDTO, ComprobantesVentasCabEntity.class);
            ComprobantesVentasCabEntity savedEntity = iComprobantesVentasCabRepository.save(comprobantesVentasCabEntity);
            // Crear detalles de venta
            savedEntity.getComprobantesVentasDetEntity().forEach(comprobantesVentasDetEntity -> {
                ComprobanteDetalleRequest com = ComprobanteDetalleRequest.builder()
                        .cantidad(comprobantesVentasDetEntity.getCantidad())
                        .idProducto(comprobantesVentasDetEntity.getIdProducto())
                        .build();
                comprobanteDetalleRequests.add(com);
            });

            // Publicar evento de venta creada
            VentaCreadaEvent event = VentaCreadaEvent.builder()
                    .idComprobanteVenta(savedEntity.getId())
                    .moneda(savedEntity.getCodigoMoneda())
                    .idUsuario(savedEntity.getUsuarioCreacion())
                    .fechaCobro(savedEntity.getFechaEmision())
                    .formasDeCobrosRequest(formasDeCobrosRequest)
                    .idEmpresa(savedEntity.getIdEmpresa())
                    .comprobanteDetalleRequest(comprobanteDetalleRequests)
                    .idAlmacen(ventaRequest.getIdAlmacen())
                            .build();
            rabbitTemplate.convertAndSend("ventasExchange", "venta.creada", event);

            return modelMapper.map(savedEntity, ComprobantesVentasCabDTO.class);
        } catch (Exception e) {
            throw new ComprobanteVentaException("Error al guardar el comprobante de venta: " + e.getMessage());
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
                System.out.println("Venta ya fue anulada, no se requiere acción: " + id);
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
            System.out.println("Ejecutando consulta con serie: " + serie + ", idPuntoVenta: " + idPuntoVenta);
            String serieMax = iComprobantesVentasCabRepository.findMaxNumero(serie, idPuntoVenta);
            System.out.println("Resultado de la consulta: " + serieMax);

            if (serieMax == null || serieMax.equals("")) {
                serieMax = "1";
            }
            return serieMax;
        } catch (Exception e) {
            System.err.println("Error al ejecutar la consulta: " + e.getMessage());
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
                System.out.println("Numero: " + numero);
                emitter.send(numero);
            } catch (IOException e) {
                System.err.println("Error en la tarea asíncrona: " + e.getMessage());
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

    @RabbitListener(queues = "ventasCompensarQueue")
    public void recibirMensajeCompensacion(VentaCompensarEvent event) {
        try {
            Boolean anulada = this.anularVenta(event.getVentaId());
            if (anulada) {
                System.out.println("Venta compensada y anulada: " + event.getVentaId());
                throw new ComprobanteVentaException("Se produjo un error al registrar el cobro, se anulo la venta");
            }
        } catch (Exception e) {
            System.err.println("Error al compensar la venta: " + e.getMessage());
            throw new ComprobanteVentaException("Error al anular la venta: " + e.getMessage());
        }
    }
}
