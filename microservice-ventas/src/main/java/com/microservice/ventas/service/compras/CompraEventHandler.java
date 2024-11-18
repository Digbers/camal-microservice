package com.microservice.ventas.service.compras;

import com.microservice.ventas.entity.ComprobantesComprasCaEntity;
import com.microservice.ventas.event.CompraFailedEvent;
import com.microservice.ventas.repository.IComprobanteCompraCaRepository;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
@RequiredArgsConstructor
public class CompraEventHandler {
    private final Map<Long, CompletableFuture<Boolean>> sagaCompletions = new ConcurrentHashMap<>();
    private final IComprobanteCompraCaRepository comprobanteCompraCaRepository;

    public CompletableFuture<Boolean> registerSagaCompletion(Long compraId) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        sagaCompletions.put(compraId, future);
        return future;
    }
    @RabbitListener(queues = "InventarioErrorActualizandoComprasQueue")
    public void handleInventarioErrorActualizando(CompraFailedEvent event, Channel channel, Message message) {
        log.error("Error al actualizar el inventario, se procede a compensar la compra: " + event.getId());
        try {
            // Validar que el ID no sea nulo
            if (event.getId() == null) {
                log.error("ID de compra nulo recibido en evento de fallo");
                // Rechazar el mensaje sin reintento ya que es un error irrecuperable
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
                return;
            }

            ComprobantesComprasCaEntity comprobante = comprobanteCompraCaRepository
                    .findById(event.getId())
                    .orElseThrow(() -> new RuntimeException("Comprobante no encontrado: " + event.getId()));

            comprobante.setEstadoCreacion("FALLO_STOCK");
            comprobanteCompraCaRepository.save(comprobante);

            // Completar el Future correspondiente
            CompletableFuture<Boolean> future = sagaCompletions.remove(event.getId());
            if (future != null) {
                future.complete(true);
            }

            // Confirmar procesamiento exitoso del mensaje
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            log.info("Procesado correctamente el error de inventario para la compra ID: {}", event.getId());
        } catch (Exception e) {
            log.error("Error al eliminar el comprobante de compra: " + e.getMessage());
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
    @RabbitListener(queues = "FinanzasPagosErrorQueue")
    public void handleFinanzasPagoFallido(CompraFailedEvent event, Channel channel, Message message) {
        log.error("Inventario fallido para la compra: " + event.getId()+ " del source: " + event.getSource());
        try {
            // Validar que el ID no sea nulo
            if (event.getId() == null) {
                log.error("ID de compra nulo recibido en evento de fallo");
                // Rechazar el mensaje sin reintento ya que es un error irrecuperable
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
                return;
            }

            ComprobantesComprasCaEntity comprobante = comprobanteCompraCaRepository
                    .findById(event.getId())
                    .orElseThrow(() -> new RuntimeException("Comprobante no encontrado: " + event.getId()));

            comprobante.setEstadoCreacion("FALLO_PAGO");
            comprobanteCompraCaRepository.save(comprobante);
            // Completar el Future correspondiente
            CompletableFuture<Boolean> future = sagaCompletions.remove(event.getId());
            if (future != null) {
                future.complete(true);
            }

            // Confirmar procesamiento exitoso del mensaje
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            log.info("Procesado correctamente el fallo de pago para la compra ID: {}", event.getId());

        } catch (Exception e) {
            log.error("Error procesando fallo de pago: " + e.getMessage(), e);
            try {
                // En caso de error, rechazar el mensaje sin reintento
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);

                CompletableFuture<Boolean> future = sagaCompletions.remove(event.getId());
                if (future != null) {
                    future.completeExceptionally(e);
                }
            } catch (Exception ioException) {
                log.error("Error al rechazar mensaje RabbitMQ", ioException);
            }
        }
    }
    @RabbitListener(queues = "FinanzasPagosSuccessQueue")
    public void handleFinanzasPagoExitoso(Long idComprobanteCompra, Channel channel, Message message) {
        log.info("Recibido evento de éxito en finanzas para la compra ID: {}", idComprobanteCompra);

        try {
            if (idComprobanteCompra == null) {
                log.error("ID de compra nulo recibido en evento de éxito");
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
                return;
            }

            ComprobantesComprasCaEntity comprobante = comprobanteCompraCaRepository
                    .findById(idComprobanteCompra)
                    .orElseThrow(() -> new RuntimeException("Comprobante no encontrado: " + idComprobanteCompra));

            comprobante.setEstadoCreacion("SUCCESS");
            comprobanteCompraCaRepository.save(comprobante);

            // Completar el Future correspondiente
            CompletableFuture<Boolean> future = sagaCompletions.remove(idComprobanteCompra);
            if (future != null) {
                log.info("Completando el future para la compra ID: {}", idComprobanteCompra);
                future.complete(true);
            }
            // Confirmar procesamiento exitoso del mensaje
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            log.info("Procesado correctamente el éxito de pago para la compra ID: {}", idComprobanteCompra);

        } catch (Exception e) {
            log.error("Error procesando éxito de pago: " + e.getMessage(), e);
            try {
                // En caso de error, rechazar el mensaje sin reintento
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);

                CompletableFuture<Boolean> future = sagaCompletions.remove(idComprobanteCompra);
                if (future != null) {
                    future.completeExceptionally(e);
                }
            } catch (Exception ioException) {
                log.error("Error al rechazar mensaje RabbitMQ", ioException);
            }
        }
    }

}
