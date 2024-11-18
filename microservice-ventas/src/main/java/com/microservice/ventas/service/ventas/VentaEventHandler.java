package com.microservice.ventas.service.ventas;

import com.microservice.ventas.entity.ComprobantesVentasCabEntity;
import com.microservice.ventas.event.VentaFailedEvent;
import com.microservice.ventas.repository.IcomprobantesVentasCabRepository;
import com.rabbitmq.client.Channel;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
@RequiredArgsConstructor
public class VentaEventHandler {
    private final Map<Long, CompletableFuture<Boolean>> sagaCompletions = new ConcurrentHashMap<>();
    private final IcomprobantesVentasCabRepository comprobantesVentasCabRepository;

    public CompletableFuture<Boolean> registerSagaCompletion(Long ventaId) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        sagaCompletions.put(ventaId, future);
        return future;
    }
    @RabbitListener(queues = "InventarioErrorActualizandoQueue")
    @Transactional
    public void handleInventarioErrorActualizando(VentaFailedEvent event, Channel channel, Message message) {
        try {
            log.error("Error al actualizar el inventario, se procede a compensar la venta: " + event.getId());
            // Validar que el ID no sea nulo
            if (event.getId() == null) {
                log.error("ID de venta nulo recibido en evento de fallo");
                // Rechazar el mensaje sin reintento ya que es un error irrecuperable
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
                return;
            }

            ComprobantesVentasCabEntity comprobante = comprobantesVentasCabRepository
                    .findById(event.getId())
                    .orElseThrow(() -> new RuntimeException("Comprobante no encontrado: " + event.getId()));

            comprobante.setEstadoCreacion("FALLO_STOCK");
            comprobantesVentasCabRepository.save(comprobante);

            // Completar el Future correspondiente
            CompletableFuture<Boolean> future = sagaCompletions.remove(event.getId());
            if (future != null) {
                future.complete(true);
            }

            // Confirmar procesamiento exitoso del mensaje
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            log.info("Procesado correctamente el error de inventario para la venta ID: {}", event.getId());
        } catch (Exception e) {
            log.error("Error al eliminar el comprobante de venta: " + e.getMessage());
            try {
                // En caso de error, rechazar el mensaje sin reintento
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
                CompletableFuture<Boolean> future = sagaCompletions.remove(event.getId());
                if (future != null) {
                    future.completeExceptionally(e);
                }
            } catch (IOException ioException) {
                log.error("Error al rechazar mensaje RabbitMQ", ioException);
            }
        }
    }

    @RabbitListener(queues = "FinanzasCobrosErrorQueue")
    @Transactional
    public void handleFinanzasCobroFallido(VentaFailedEvent event, Channel channel, Message message) {
        log.info("Recibido evento de fallo en finanzas para la venta ID: {}", event.getId());

        try {
            // Validar que el ID no sea nulo
            if (event.getId() == null) {
                log.error("ID de venta nulo recibido en evento de fallo");
                // Rechazar el mensaje sin reintento ya que es un error irrecuperable
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
                return;
            }

            ComprobantesVentasCabEntity comprobante = comprobantesVentasCabRepository
                    .findById(event.getId())
                    .orElseThrow(() -> new RuntimeException("Comprobante no encontrado: " + event.getId()));

            comprobante.setEstadoCreacion("FALLO_PAGO");
            comprobantesVentasCabRepository.save(comprobante);

            // Completar el Future correspondiente
            CompletableFuture<Boolean> future = sagaCompletions.remove(event.getId());
            if (future != null) {
                future.complete(true);
            }

            // Confirmar procesamiento exitoso del mensaje
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            log.info("Procesado correctamente el fallo de pago para la venta ID: {}", event.getId());

        } catch (Exception e) {
            log.error("Error procesando fallo de pago: " + e.getMessage(), e);
            try {
                // En caso de error, rechazar el mensaje sin reintento
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);

                CompletableFuture<Boolean> future = sagaCompletions.remove(event.getId());
                if (future != null) {
                    future.completeExceptionally(e);
                }
            } catch (IOException ioException) {
                log.error("Error al rechazar mensaje RabbitMQ", ioException);
            }
        }
    }

    @RabbitListener(queues = "FinanzasCobrosSuccessQueue")
    public void handleFinanzasCobroExitoso(Long idComprobanteVenta, Channel channel, Message message) {
        log.info("Recibido evento de éxito en finanzas para la venta ID: {}", idComprobanteVenta);

        try {
            if (idComprobanteVenta == null) {
                log.error("ID de venta nulo recibido en evento de éxito");
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
                return;
            }

            ComprobantesVentasCabEntity comprobante = comprobantesVentasCabRepository
                    .findById(idComprobanteVenta)
                    .orElseThrow(() -> new RuntimeException("Comprobante no encontrado: " + idComprobanteVenta));

            comprobante.setEstadoCreacion("SUCCESS");
            comprobantesVentasCabRepository.save(comprobante);

            // Completar el Future correspondiente
            CompletableFuture<Boolean> future = sagaCompletions.remove(idComprobanteVenta);
            if (future != null) {
                log.info("Completando el future para la venta ID: {}", idComprobanteVenta);
                future.complete(true);
            }
            // Confirmar procesamiento exitoso del mensaje
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            log.info("Procesado correctamente el éxito de pago para la venta ID: {}", idComprobanteVenta);

        } catch (Exception e) {
            log.error("Error procesando éxito de pago: " + e.getMessage(), e);
            try {
                // En caso de error, rechazar el mensaje sin reintento
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);

                CompletableFuture<Boolean> future = sagaCompletions.remove(idComprobanteVenta);
                if (future != null) {
                    future.completeExceptionally(e);
                }
            } catch (IOException ioException) {
                log.error("Error al rechazar mensaje RabbitMQ", ioException);
            }
        }
    }
}