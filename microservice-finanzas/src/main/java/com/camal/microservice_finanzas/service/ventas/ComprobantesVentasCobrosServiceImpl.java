package com.camal.microservice_finanzas.service.ventas;

import com.camal.microservice_finanzas.event.CompensarVentaEvent;
import com.camal.microservice_finanzas.event.VentaCreadaEvent;
import com.camal.microservice_finanzas.exception.ComprobantesVentasCobrosException;
import com.camal.microservice_finanzas.persistence.entity.ComprobantesVentasCobrosEntity;
import com.camal.microservice_finanzas.persistence.entity.FormasCobrosEntity;
import com.camal.microservice_finanzas.persistence.entity.MonedasEntity;
import com.camal.microservice_finanzas.persistence.repository.IComprobantesVentasCobrosRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ComprobantesVentasCobrosServiceImpl {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private IComprobantesVentasCobrosRepository iComprobantesVentasCobrosRepository;

    @Autowired
    private IFormasCobrosRepository iFormasCobrosRepository;

    @Autowired
    private IMonedasRepository iMonedasRepository;

    @RabbitListener(queues = "ventasQueue")
    public void handleVentaCreadaEvent(VentaCreadaEvent event) {
        try {
            Optional<MonedasEntity> moneda = iMonedasRepository.findById(event.getMoneda());
            if(moneda.isEmpty()) {
                throw new ComprobantesVentasCobrosException("La moneda con ID " + event.getMoneda() + " no existe.");
            }
            MonedasEntity monedaEntity = moneda.get();
            ComprobantesVentasCobrosEntity comprobantesVentasCobros = ComprobantesVentasCobrosEntity.builder()
                    .idComprobanteVenta(event.getIdComprobanteVenta())
                    .fechaCobro(event.getFechaCobro())
                    .idEmpresa(event.getIdEmpresa())
                    .monedasEntity(monedaEntity)
                    .usuarioCreacion(event.getIdUsuario())
                    .build();
            event.getFormasDeCobrosRequest().getFormasDeCobro().forEach(formaDeCobro -> {
                    Optional<FormasCobrosEntity> formaCobro = iFormasCobrosRepository.findById(formaDeCobro.getCodigo());
                    if(formaCobro.isEmpty()) {
                        throw new ComprobantesVentasCobrosException("La forma de cobro con ID " + formaDeCobro.getCodigo() + " no existe.");
                    }
                    FormasCobrosEntity formasCobrosEntity = formaCobro.get();
                    comprobantesVentasCobros.setFormasCobrosEntity(formasCobrosEntity);
                    comprobantesVentasCobros.setMontoCobrado(formaDeCobro.getMonto());
                    iComprobantesVentasCobrosRepository.save(comprobantesVentasCobros);
            });
        } catch (Exception e) {
            CompensarVentaEvent compensarVentaEvent = CompensarVentaEvent.builder()
                    .ventaId(event.getIdComprobanteVenta())
                    .comprobanteDetalleRequest(event.getComprobanteDetalleRequest())
                    .idAlmacen(event.getIdAlmacen())
                    .build();
            rabbitTemplate.convertAndSend("ventasExchange", "venta.compensar", compensarVentaEvent);
        }
    }
}
