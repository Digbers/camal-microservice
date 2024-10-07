package com.camal.microservice_finanzas.service.ventas;

import com.camal.microservice_finanzas.event.*;
import com.camal.microservice_finanzas.exception.ComprobantesVentasCobrosException;
import com.camal.microservice_finanzas.persistence.entity.ComprobantesVentasCobrosEntity;
import com.camal.microservice_finanzas.persistence.entity.FormasCobrosEntity;
import com.camal.microservice_finanzas.persistence.entity.MonedasEntity;
import com.camal.microservice_finanzas.persistence.repository.IComprobantesVentasCobrosRepository;
import com.camal.microservice_finanzas.persistence.repository.IFormasCobrosRepository;
import com.camal.microservice_finanzas.persistence.repository.IMonedasRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@Slf4j
@RequiredArgsConstructor
public class ComprobantesVentasCobrosServiceImpl {

    private final RabbitTemplate rabbitTemplate;
    private final IComprobantesVentasCobrosRepository iComprobantesVentasCobrosRepository;
    private final IFormasCobrosRepository iFormasCobrosRepository;
    private final IMonedasRepository iMonedasRepository;

    @RabbitListener(queues = "VentaInventarioActualizadoQueue")
    public void handleVentaCreadaEvent(InventarioActualizadoVentasEvent event) {
        try {
            if(!event.getCodigoFormaPago().equals("CRE")){
                MonedasEntity moneda = iMonedasRepository.findById(event.getComprobantesComprasCa().getIdMoneda()).orElseThrow(() -> new ComprobantesVentasCobrosException("Moneda no encontrada"));
                FormasCobrosEntity fromaCobro = iFormasCobrosRepository.findById(event.getCodigoFormaPago()).orElseThrow(() -> new ComprobantesVentasCobrosException("Forma de cobro no encontrada"));
                ComprobantesVentasCobrosEntity comprobantesCobros = ComprobantesVentasCobrosEntity.builder()
                        .idEmpresa(event.getComprobantesComprasCa().getIdEmpresa())
                        .idComprobanteVenta(event.getComprobantesComprasCa().getId())
                        .formasCobrosEntity(fromaCobro)
                        .montoCobrado(event.getMontoTotal())
                        .fechaCobro(LocalDate.now())
                        .monedasEntity(moneda)
                        .usuarioCreacion(event.getComprobantesComprasCa().getUsuarioCreacion())
                        .build();
                iComprobantesVentasCobrosRepository.save(comprobantesCobros);
                log.info("ComprobantesVentasCobros creado con id: " + comprobantesCobros.getId());
            }
        } catch (Exception e) {
            VentaFailedEvent ventaFailedEvent = VentaFailedEvent.builder()
                    .id(event.getComprobantesComprasCa().getId())
                    .source("finanzas")
                    .build();
            rabbitTemplate.convertAndSend("FinanzasVentasExchange", "finanzas.error-cobros", ventaFailedEvent);
        }
    }
    @RabbitListener(queues = "VentaCompensarQueue")
    public void handleCompensacionCompra(CompensacionVentaEvent event) {
        if (event.getSource().equals("finanzas")) {
            log.info("Compensación ejemplo en fiananzas para la venta: " + event.getId());
        } else {
            log.info("Ignorando compensación en Finanzas porque la fuente de error es: " + event.getSource());
        }
    }
    @RabbitListener(queues = "CompraCompensarQueue")
    public void handleCompensacionCompra(CompensacionCompraEvent event) {
        if (event.getSource().equals("finanzas")) {
            log.info("Compensación ejemplo en fiananzas para la compra: " + event.getId());
        } else {
            log.info("Ignorando compensación en Finazas porque la fuente de error es: " + event.getSource());
        }
    }

}
