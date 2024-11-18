package com.microservice.inventario.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    @Bean
    public ConnectionFactory connectionFactory() {
        return new CachingConnectionFactory("localhost");
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        // Crear y configurar ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //para las fechas
        objectMapper.registerModule(new JavaTimeModule());

        // Crear el conversor con el ObjectMapper configurado
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);

        // Configurar el type mapper
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.setTrustedPackages(
                "com.microservice.inventario.event",// Paquete de eventos de ventas
                "com.microservice.ventas.event",      // Paquete de eventos de ventas
                "com.camal.microservice_finanzas.event"    // Paquete de eventos de finanzas
        );

        converter.setJavaTypeMapper(typeMapper);

        return converter;
    }

    @Bean
    public TopicExchange ventasExchange() {
        return new TopicExchange("VentasExchange");
    }
    @Bean
    public TopicExchange inventarioVentasExchage() {
        return new TopicExchange("InventarioVentasExchange");
    }
    @Bean
    public TopicExchange compensacionVentasExchange() {
        return new TopicExchange("CompensacionVentasExchange");
    }
    @Bean
    public Queue ventaCreadaQueue() {
        return new Queue("VentaCreadaQueue");
    }
    @Bean
    public Queue inventarioVentasActualizadoQueue() {
        return new Queue("InventarioVentasActualizadoQueue");
    }
    @Bean
    public Queue errorActualizandoVentasQueue() {
        return new Queue("InventarioErrorActualizandoQueue");
    }
    @Bean
    public Queue ventaCompensarQueue() {
        return new Queue("VentaCompensarQueue");
    }
    @Bean
    public Binding ventasBinding(Queue ventaCreadaQueue, TopicExchange ventasExchange) {
        return BindingBuilder.bind(ventaCreadaQueue).to(ventasExchange).with("venta.creada");
    }
    @Bean
    public Binding ventaCompensarBinding(Queue ventaCompensarQueue, TopicExchange compensacionVentasExchange) {
        return BindingBuilder.bind(ventaCompensarQueue).to(compensacionVentasExchange).with("venta.compensar");
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
    public TopicExchange compensacionCompraExchange() {
        return new TopicExchange("CompensacionCompraExchange");
    }
    @Bean
    public TopicExchange inventarioCompraExchange() {
        return new TopicExchange("InventarioCompraExchange");
    }
    @Bean
    public Queue compraCreadaQueue() {
        return new Queue("CompraCreadaQueue");
    }

    @Bean
    public Queue inventarioActualizadoComprasQueue() {
        return new Queue("InventarioActualizadoComprasQueue");
    }
    @Bean
    public Queue errorActualizandoComprasQueue() {
        return new Queue("InventarioErrorActualizandoComprasQueue");
    }
    @Bean
    public Queue compraCompensarQueue() {
        return new Queue("CompraCompensarQueue");
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
        return BindingBuilder.bind(errorActualizandoComprasQueue).to(inventarioCompraExchange).with("inventario.error-actualizando-c");
    }
    @Bean
    public Binding compensacionBinding(Queue compraCompensarQueue, TopicExchange compensacionCompraExchange) {
        return BindingBuilder.bind(compraCompensarQueue).to(compensacionCompraExchange).with("compra.compensar");
    }
}
