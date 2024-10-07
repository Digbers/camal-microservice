package com.microservice.inventario.config;

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
    public TopicExchange CompensacionVentasExchange() {
        return new TopicExchange("CompensacionVentasExchange");
    }
    @Bean
    public Queue ventaCreadaQueue() {
        return new Queue("VentaCreadaQueue", false);
    }
    @Bean
    public Queue inventarioVentasActualizadoQueue() {
        return new Queue("InventarioVentasActualizadoQueue", false);    }
    @Bean
    public Queue errorActualizandoVentasQueue() {
        return new Queue("InventarioErrorActualizandoQueue", false);
    }
    @Bean
    public Queue ventasCompensarQueue() {
        return new Queue("VentasCompensarQueue");
    }
    @Bean
    public Binding ventasBinding(Queue ventaCreadaQueue, TopicExchange ventasExchange) {
        return BindingBuilder.bind(ventaCreadaQueue).to(ventasExchange).with("venta.creada");
    }
    @Bean
    public Binding ventaCompensarBinding(Queue ventasCompensarQueue, TopicExchange CompensacionVentasExchange) {
        return BindingBuilder.bind(ventasCompensarQueue).to(CompensacionVentasExchange).with("venta.compensar");
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
    public TopicExchange CompensacionExchange() {
        return new TopicExchange("CompensacionExchange");
    }
    @Bean
    public TopicExchange inventarioCompraExchange() {
        return new TopicExchange("InventarioCompraExchange");
    }
    @Bean
    public Queue compraCreadaQueue() {
        return new Queue("CompraCreadaQueue", false);
    }

    @Bean
    public Queue inventarioActualizadoComprasQueue() {
        return new Queue("InventarioActualizadoComprasQueue", false);
    }
    @Bean
    public Queue errorActualizandoComprasQueue() {
        return new Queue("InventarioErrorActualizandoComprasQueue", false);
    }
    @Bean
    public Queue compensacionCompraQueue() {
        return new Queue("CompensacionCompraQueue", false);
    }
    @Bean
    public Binding compraCreadaBinding(Queue compraCreadaQueue, TopicExchange compraExchange) {
        return BindingBuilder.bind(compraCreadaQueue).to(compraExchange).with("compra.creada");
    }

    @Bean
    public Binding inventarioActualizadoBinding(Queue inventarioActualizadoComprasQueue, TopicExchange inventarioCompraExchange) {
        return BindingBuilder.bind(inventarioActualizadoComprasQueue).to(inventarioCompraExchange).with("inventario.actualizado");
    }

    @Bean
    public Binding inventarioErrorActualizandoBinding(Queue errorActualizandoComprasQueue, TopicExchange inventarioCompraExchange) {
        return BindingBuilder.bind(errorActualizandoComprasQueue).to(inventarioCompraExchange).with("inventario.erroractualizando");
    }
    @Bean
    public Binding compensacionBinding(Queue compensacionCompraQueue, TopicExchange CompensacionExchange) {
        return BindingBuilder.bind(compensacionCompraQueue).to(CompensacionExchange).with("compra.compensar");
    }
}
