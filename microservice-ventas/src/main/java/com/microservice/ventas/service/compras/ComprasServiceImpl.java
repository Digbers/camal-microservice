package com.microservice.ventas.service.compras;

import com.microservice.ventas.client.EmpresaClient;
import com.microservice.ventas.controller.DTO.ComprobanteDetalleRequest;
import com.microservice.ventas.controller.DTO.compras.CompraRequest;
import com.microservice.ventas.controller.DTO.compras.ComprobantesComprasCaDTO;
import com.microservice.ventas.entity.ComprobantesComprasCaEntity;
import com.microservice.ventas.event.CompensacionCompraEvent;
import com.microservice.ventas.event.CompraCreadaEvent;
import com.microservice.ventas.event.CompraFailedEvent;
import com.microservice.ventas.exception.ComprobanteCompraException;
import com.microservice.ventas.exception.EmpresaNoEncontradaException;
import com.microservice.ventas.repository.IComprobanteCompraCaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
@RequiredArgsConstructor
public class ComprasServiceImpl implements IComprasService {
    private final IComprobanteCompraCaRepository iComprobanteCompraCaRepository;
    private final ModelMapper modelMapper;
    private final EmpresaClient empresaClient;
    private final RabbitTemplate rabbitTemplate;


    @Override
    public ComprobantesComprasCaDTO save(CompraRequest compraRequest) {

        ComprobantesComprasCaDTO comprobantesComprasCaDTO = compraRequest.getComprobantesComprasCa();
        boolean exist = empresaClient.verificarEmpresaExiste(comprobantesComprasCaDTO.getIdEmpresa());
        if (!exist) {
            throw new EmpresaNoEncontradaException("La empresa con ID " + comprobantesComprasCaDTO.getIdEmpresa() + " no existe.");
        }
        try {
            ComprobantesComprasCaEntity comprobantesComprasCaEntity = modelMapper.map(comprobantesComprasCaDTO, ComprobantesComprasCaEntity.class);
            ComprobantesComprasCaEntity savedEntity = iComprobanteCompraCaRepository.save(comprobantesComprasCaEntity);
            ComprobantesComprasCaDTO comprobanteCompraRes = modelMapper.map(savedEntity, ComprobantesComprasCaDTO.class);
            CompraCreadaEvent event = CompraCreadaEvent.builder()
                    .codigoFormaPago(compraRequest.getCodigoFormaPago())
                    .comprobantesComprasCa(comprobanteCompraRes)
                    .idAlmacen(compraRequest.getIdAlmacen())
                    .build();
            rabbitTemplate.convertAndSend("CompraExchange", "compra.creada", event);
            return comprobanteCompraRes;
        } catch (Exception e) {
            throw new ComprobanteCompraException("Error al guardar el comprobante de compra: " + e.getMessage());
        }
    }
    @RabbitListener(queues = "FinanzasPagosErrorQueue")
    public void handleInventarioActualizacionFallida(CompraFailedEvent event) {
        log.error("Inventario fallido para la compra: " + event.getId()+ " del source: " + event.getSource());
        emitirEventoCompensacion(event.getId(), event.getSource());
    }

    @RabbitListener(queues = "FinanzasPagosErrorQueue")
    public void handleFinanzasPagoFallido(CompraFailedEvent event) {
        log.error("Pago fallido en finanzas para la compra: " + event.getId() + " del source: " + event.getSource());
        emitirEventoCompensacion(event.getId(), event.getSource());
    }
    @Transactional
    private void emitirEventoCompensacion(Long compraId, String source) {
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
                    .build();
            log.info("Compensación iniciada para la compra: " + compraId + " debido a fallo en " + source);
            log.info("Recibido evento de compensacion de compra");

            iComprobanteCompraCaRepository.deleteById(compraId);
            rabbitTemplate.convertAndSend("CompensacionExchange", "compra.compensar", compensacionEvent);
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
}
