package com.camal.microservice_finanzas.service.ventas;

import com.camal.microservice_finanzas.event.*;
import com.camal.microservice_finanzas.persistence.entity.ComprobantesVentasCobrosEntity;
import com.camal.microservice_finanzas.persistence.entity.FormasCobrosEntity;
import com.camal.microservice_finanzas.persistence.entity.MonedasEntity;
import com.camal.microservice_finanzas.persistence.repository.IComprobantesVentasCobrosRepository;
import com.camal.microservice_finanzas.persistence.repository.IFormasCobrosRepository;
import com.camal.microservice_finanzas.persistence.repository.IMonedasRepository;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ComprobantesVentasCobrosServiceImpl {

    private final RabbitTemplate rabbitTemplate;
    private final IComprobantesVentasCobrosRepository iComprobantesVentasCobrosRepository;
    private final IFormasCobrosRepository iFormasCobrosRepository;
    private final IMonedasRepository iMonedasRepository;
    private final ModelMapper modelMapper;

    @RabbitListener(queues = "InventarioVentasActualizadoQueue")
        public void handleVentaCreadaEvent(InventarioActualizadoVentasEvent event, Channel channel, Message message) throws IOException {
        try {
            if(!event.getCodigoFormaPago().equals("CRE") && !event.getComprobantesVentasCobrosDTO().isEmpty()){
                event.getComprobantesVentasCobrosDTO().forEach(cVentasCobrosDTO -> {
                    Optional<FormasCobrosEntity> formasCobros = iFormasCobrosRepository.findByIdEmpresaAndCodigo(cVentasCobrosDTO.getIdEmpresa(), cVentasCobrosDTO.getFormasDeCobros());
                    if(formasCobros.isEmpty()){
                        throw new RuntimeException("Forma de cobro no encontrada con ID: " + cVentasCobrosDTO.getFormasDeCobros());
                    }
                    Optional<MonedasEntity> monedasEntity = iMonedasRepository.findByIdEmpresaAndCodigo(cVentasCobrosDTO.getIdEmpresa(), cVentasCobrosDTO.getMoneda());
                    if(monedasEntity.isEmpty()){
                        throw new RuntimeException("Moneda no encontrada con ID: " + cVentasCobrosDTO.getMoneda());
                    }
                    ComprobantesVentasCobrosEntity comprobantesCobros = ComprobantesVentasCobrosEntity.builder()
                            .idEmpresa(cVentasCobrosDTO.getIdEmpresa())
                            .idComprobanteVenta(event.getComprobantesVentasCab().getId())
                            .formasCobrosEntity(formasCobros.get())
                            .montoCobrado(cVentasCobrosDTO.getMontoCobrado())
                            .fechaCobro(cVentasCobrosDTO.getFechaCobro())
                            .descripcion(cVentasCobrosDTO.getDescripcion())
                            .monedasEntity(monedasEntity.get())
                            .usuarioCreacion(cVentasCobrosDTO.getUsuarioCreacion())
                            .build();
                    iComprobantesVentasCobrosRepository.save(comprobantesCobros);
                });
            }
            log.info("ComprobantesVentasCobros creado con id: " + event.getComprobantesVentasCab().getId());

            // Enviar confirmación de éxito
            rabbitTemplate.convertAndSend("FinanzasVentasExchange", "finanzas.success-cobros", event.getComprobantesVentasCab().getId());

        } catch (Exception e) {
            log.error("Error al procesar comprobantesVentasCab", e);
            log.info("Id Comrprobante venta: " + event.getComprobantesVentasCab().getId());
            VentaFailedEvent ventaFailedEvent = VentaFailedEvent.builder()
                    .id(event.getComprobantesVentasCab().getId())
                    .source("finanzas")
                    .build();
            rabbitTemplate.convertAndSend("FinanzasVentasExchange", "finanzas.error-cobros", ventaFailedEvent);
        } finally {
            // Confirmar manualmente el mensaje
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }
    }

}
