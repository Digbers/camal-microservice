package com.camal.microservice_finanzas.service.compras;

import com.camal.microservice_finanzas.event.CompraFailedEvent;
import com.camal.microservice_finanzas.event.InventarioActualizadoCompraEvent;
import com.camal.microservice_finanzas.persistence.entity.ComprobantesComprasPagosEntity;
import com.camal.microservice_finanzas.persistence.entity.FormasPagosEntity;
import com.camal.microservice_finanzas.persistence.entity.MonedasEntity;
import com.camal.microservice_finanzas.persistence.repository.IComprobantesComprasPagosRepository;
import com.camal.microservice_finanzas.persistence.repository.IFormasPagosRepository;
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
public class ComprobantesComprasPagosServiceImpl {
    private final IComprobantesComprasPagosRepository iComprobantesComprasPagosRepository;
    private final IFormasPagosRepository iFormasPagosRepository;
    private final RabbitTemplate rabbitTemplate;
    private final IMonedasRepository iMonedasRepository;

    @RabbitListener(queues = "InventarioActualizadoQueue")
    public void handleInventarioActualizadoEvent(InventarioActualizadoCompraEvent event) {
        log.info("Recibido evento de Inventario Actualizado");
        try {
            // Obtener entidad de forma de pago
            FormasPagosEntity formaPago = iFormasPagosRepository.findById(event.getCodigoFormaPago()).orElseThrow(() -> new RuntimeException("Forma de pago no encontrada"));

            if (formaPago.getCodigo().equals("CAN")) {
                log.info("Se ha detectado un CANCELADO");
                MonedasEntity moneda = iMonedasRepository.findById(event.getComprobantesComprasCa().getIdMoneda()).orElseThrow(() -> new RuntimeException("Moneda no encontrada"));
                // Crear la entidad a partir del evento recibido
                ComprobantesComprasPagosEntity nuevoPago = new ComprobantesComprasPagosEntity();
                nuevoPago.setIdEmpresa(event.getComprobantesComprasCa().getIdEmpresa());
                nuevoPago.setIdComprobanteCompra(event.getComprobantesComprasCa().getId());
                nuevoPago.setFormaPagosEntity(formaPago); // Obtener entidad de forma de pago
                nuevoPago.setMontoCobrado(event.getMontoTotal()); // Monto calculado
                nuevoPago.setFechaCobro(LocalDate.now());
                nuevoPago.setMonedasEntity(moneda);
                nuevoPago.setUsuarioCreacion(event.getComprobantesComprasCa().getUsuarioCreacion());

                iComprobantesComprasPagosRepository.save(nuevoPago);
                log.info("Pago registrado con éxito para el ID de Compra: {}", event.getComprobantesComprasCa().getId());
            }else{
                log.info("La forma de pago es a Credito, no se registra.");
            }

        } catch (Exception e) {
            log.error("Error al procesar el evento de Inventario Actualizado: " + e.getMessage());
            CompraFailedEvent compraFail = CompraFailedEvent.builder()
                    .id(event.getComprobantesComprasCa().getId())
                    .source("finanzas")
                    .build();
            rabbitTemplate.convertAndSend("FinanzasExchange", "finanzas.error-pagos", compraFail);
        }
    }
}
