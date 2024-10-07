package com.microservice.ventas.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {

    @Bean
    public TopicExchange ventasExchange() {
        return new TopicExchange("VentasExchange");
    }
    @Bean
    public TopicExchange inventarioVentasExchage() {
        return new TopicExchange("InventarioVentasExchange");
    }
    @Bean
    public TopicExchange finanzasVentasExchange() {
        return new TopicExchange("FinanzasVentasExchange");
    }
    @Bean
    public TopicExchange compensacionVentasExchange() {
        return new TopicExchange("CompensacionVentasExchange");
    }
    @Bean
    public Queue inventarioVentasActualizadoQueue() {
        return new Queue("InventarioVentasActualizadoQueue", false);    }
    @Bean
    public Queue errorActualizandoVentasQueue() {
        return new Queue("InventarioErrorActualizandoQueue", false);
    }
    @Bean
    public Queue ventaCreadaQueue() {
        return new Queue("VentaCreadaQueue", false);
    }
    @Bean
    public Queue ventasCompensarQueue() {
        return new Queue("VentasCompensarQueue");
    }
    @Bean
    public Queue finanzasCobrosErrorQueue() {
        return new Queue("FinanzasCobrosErrorQueue", false);
    }

    @Bean
    public Binding ventasBinding(Queue ventaCreadaQueue, TopicExchange ventasExchange) {
        return BindingBuilder.bind(ventaCreadaQueue).to(ventasExchange).with("venta.creada");
    }

    @Bean
    public Binding ventaCompensarBinding(Queue ventasCompensarQueue, TopicExchange compensacionVentasExchange) {
        return BindingBuilder.bind(ventasCompensarQueue).to(compensacionVentasExchange).with("venta.compensar");
    }
    @Bean
    public Binding finanzasCobrosErrorBinding(Queue finanzasCobrosErrorQueue, TopicExchange finanzasVentasExchange) {
        return BindingBuilder.bind(finanzasCobrosErrorQueue).to(finanzasVentasExchange).with("finanzas.error-cobros");
    }
    @Bean
    public Binding inventarioVentasActualizadoBinding(Queue inventarioVentasActualizadoQueue, TopicExchange inventarioVentasExchage) {
        return BindingBuilder.bind(inventarioVentasActualizadoQueue).to(inventarioVentasExchage).with("inventario.actualizado-ventas");
    }
    @Bean
    public Binding errorActualizandoVentasBinding(Queue errorActualizandoVentasQueue, TopicExchange inventarioVentasExchage) {
        return BindingBuilder.bind(errorActualizandoVentasQueue).to(inventarioVentasExchage).with("inventario.erroractualizado-ventas");
    }
    /*Compras*/
    @Bean
    public TopicExchange compraExchange() {
        return new TopicExchange("CompraExchange");
    }
    @Bean
    public TopicExchange compensacionExchange() {
        return new TopicExchange("CompensacionExchange");
    }
    @Bean
    public TopicExchange inventarioExchange() {
        return new TopicExchange("InventarioExchange");
    }
    @Bean
    public TopicExchange finanzasExchange() {
        return new TopicExchange("FinanzasExchange");
    }
    @Bean
    public Queue compraCreadaQueue() {
        return new Queue("CompraCreadaQueue", false);
    }
    @Bean
    public Queue errorActualizandoQueue() {
        return new Queue("InventarioErrorActualizandoQueue", false);
    }
    @Bean
    public Queue compensacionCompraQueue() {
        return new Queue("CompensacionCompraQueue", false);
    }
    @Bean
    public Queue finanzasPagosErrorQueue() {
        return new Queue("FinanzasPagosErrorQueue", false);
    }

    @Bean
    public Binding compraCreadaBinding(Queue compraCreadaQueue, TopicExchange compraExchange) {
        return BindingBuilder.bind(compraCreadaQueue).to(compraExchange).with("compra.creada");
    }
    @Bean
    public Binding compensacionBinding(Queue compensacionCompraQueue, TopicExchange compensacionExchange) {
        return BindingBuilder.bind(compensacionCompraQueue).to(compensacionExchange).with("compra.compensar");
    }
    @Bean
    public Binding inventarioErrorActualizandoBinding(Queue errorActualizandoQueue, TopicExchange inventarioExchange) {
        return BindingBuilder.bind(errorActualizandoQueue).to(inventarioExchange).with("inventario.erroractualizando");
    }
    @Bean
    public Binding finanzasPagosErrorBinding(Queue finanzasPagosErrorQueue, TopicExchange finanzasExchange) {
        return BindingBuilder.bind(finanzasPagosErrorQueue).to(finanzasExchange).with("finanzas.error-pagos");
    }
}
