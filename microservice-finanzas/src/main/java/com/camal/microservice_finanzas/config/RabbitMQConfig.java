package com.camal.microservice_finanzas.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean TopicExchange inventarioVentasExchage() {
        return new TopicExchange("InventarioVentasExchange");
    }
    @Bean
    public TopicExchange CompensacionVentasExchange() {
        return new TopicExchange("CompensacionVentasExchange");
    }
    @Bean
    public TopicExchange finanzasVentasExchange() {
        return new TopicExchange("FinanzasVentasExchange");
    }
    @Bean
    public Queue inventarioVentasActualizadoQueue() {
        return new Queue("InventarioVentasActualizadoQueue", false);    }

    @Bean
    public Queue ventasCompensarQueue() {
        return new Queue("ventasCompensarQueue");
    }
    @Bean
    public Queue finanzasCobrosErrorQueue() {
        return new Queue("FinanzasCobrosErrorQueue", false);
    }

    @Bean
    public Binding ventaCompensarBinding(Queue ventasCompensarQueue, TopicExchange ventasCompensarExchange) {
        return BindingBuilder.bind(ventasCompensarQueue).to(ventasCompensarExchange).with("venta.compensar");
    }
    @Bean
    public Binding inventarioVentasActualizadoBinding(Queue inventarioVentasActualizadoQueue, TopicExchange inventarioVentasExchage) {
        return BindingBuilder.bind(inventarioVentasActualizadoQueue).to(inventarioVentasExchage).with("inventario.actualizado-ventas");
    }
    @Bean
    public Binding finanzasCobrosErrorBinding(Queue finanzasCobrosErrorQueue, TopicExchange finanzasExchange) {
        return BindingBuilder.bind(finanzasCobrosErrorQueue).to(finanzasExchange).with("finanzas.error-cobros");
    }
    /*Compras/
     *
     * @param  ventasCompensarQueue	the queue to bind to the exchange
     * @param  ventasExchange		the exchange to bind the queue to
     * @return          	a binding between the queue and exchange
     */

    @Bean
    public TopicExchange inventarioExchange() {
        return new TopicExchange("InventarioExchange");
    }

    @Bean
    public TopicExchange finanzasExchange() {
        return new TopicExchange("FinanzasExchange");
    }
    @Bean
    public TopicExchange CompensacionExchange() {
        return new TopicExchange("CompensacionExchange");
    }
    @Bean
    public Queue inventarioActualizadoQueue() {
        return new Queue("InventarioActualizadoQueue", false);
    }

    @Bean
    public Queue finanzasPagosErrorQueue() {
        return new Queue("FinanzasPagosErrorQueue", false);
    }
    @Bean
    public Queue compensacionCompraQueue() {
        return new Queue("CompensacionCompraQueue", false);
    }

    @Bean
    public Binding inventarioActualizadoBinding(Queue inventarioActualizadoQueue, TopicExchange inventarioExchange) {
        return BindingBuilder.bind(inventarioActualizadoQueue).to(inventarioExchange).with("inventario.actualizado");
    }

    @Bean
    public Binding finanzasPagosErrorBinding(Queue finanzasPagosErrorQueue, TopicExchange finanzasExchange) {
        return BindingBuilder.bind(finanzasPagosErrorQueue).to(finanzasExchange).with("finanzas.error-pagos");
    }
    @Bean
    public Binding compensacionBinding(Queue compensacionCompraQueue, TopicExchange compensacionExchange) {
        return BindingBuilder.bind(compensacionCompraQueue).to(compensacionExchange).with("compra.compensar");
    }


}
