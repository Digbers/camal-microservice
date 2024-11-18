package com.camal.microservice_finanzas.service.compras;

import com.camal.microservice_finanzas.event.CompensacionCompraEvent;
import com.camal.microservice_finanzas.event.CompraFailedEvent;
import com.camal.microservice_finanzas.event.InventarioActualizadoCompraEvent;
import com.camal.microservice_finanzas.persistence.entity.ComprobantesComprasPagosEntity;
import com.camal.microservice_finanzas.persistence.entity.FormasPagosEntity;
import com.camal.microservice_finanzas.persistence.entity.MonedasEntity;
import com.camal.microservice_finanzas.persistence.repository.IComprobantesComprasPagosRepository;
import com.camal.microservice_finanzas.persistence.repository.IFormasPagosRepository;
import com.camal.microservice_finanzas.persistence.repository.IMonedasRepository;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Service
@Slf4j
@RequiredArgsConstructor
public class ComprobantesComprasPagosServiceImpl {
    private final IComprobantesComprasPagosRepository iComprobantesComprasPagosRepository;
    private final IFormasPagosRepository iFormasPagosRepository;
    private final RabbitTemplate rabbitTemplate;
    private final IMonedasRepository iMonedasRepository;
    //esta cola es de COMPRAS
    @RabbitListener(queues = "InventarioActualizadoComprasQueue")
    public void handleInventarioActualizadoEvent(InventarioActualizadoCompraEvent event, Channel channel, Message message) throws IOException {
        log.info("Recibido evento de Inventario Actualizado");
        try {
            //codigoFormaDePag ESTADO
            if(!event.getCodigoFormaPago().equals("CRE") && !event.getComprobantesComprasPagosDTO().isEmpty()){
                event.getComprobantesComprasPagosDTO().forEach(pago -> {
                    MonedasEntity moneda = iMonedasRepository.findById(pago.getMoneda()).orElseThrow(() -> new RuntimeException("Moneda no encontrada"));
                    FormasPagosEntity formasPagosE = iFormasPagosRepository.findById(pago.getFormaPagos()).orElseThrow(() -> new RuntimeException("Forma de pago no encontrada"));
                    ComprobantesComprasPagosEntity pagoEntity = ComprobantesComprasPagosEntity.builder()
                            .idEmpresa(pago.getIdEmpresa())
                            .idComprobanteCompra(pago.getIdComprobanteCompra())
                            .formaPagosEntity(formasPagosE)
                            .montoCobrado(pago.getMontoCobrado())
                            .fechaCobro(pago.getFechaCobro())
                            .descripcion("")
                            .monedasEntity(moneda)
                            .build();
                    iComprobantesComprasPagosRepository.save(pagoEntity);
                });
            }
            log.info("ComprobantesComprasPagos creado con id: " + event.getComprobantesComprasCa().getId());

            // Enviar confirmación de éxito
            rabbitTemplate.convertAndSend("FinanzasExchange", "finanzas.success-pagos", event.getComprobantesComprasCa().getId());

        } catch (Exception e) {
            log.error("Error al procesar el evento de Inventario Actualizado: " + e.getMessage());
            CompraFailedEvent compraFail = CompraFailedEvent.builder()
                    .id(event.getComprobantesComprasCa().getId())
                    .source("finanzas")
                    .build();
            rabbitTemplate.convertAndSend("FinanzasExchange", "finanzas.error-pagos", compraFail);
        }   finally {
            // Confirmar manualmente el mensaje
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }
    }

}
