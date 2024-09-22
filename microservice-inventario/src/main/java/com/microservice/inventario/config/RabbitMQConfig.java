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
        return new TopicExchange("ventasExchange");
    }

    @Bean
    public Queue inventarioQueue() {
        return new Queue("inventarioQueue");
    }

    @Bean
    public Queue inventarioCompensarQueue() {
        return new Queue("inventarioCompensarQueue");
    }

    // Binding para asociar la cola del inventario con el intercambio y la clave de ruteo `venta.creada`
    @Bean
    public Binding inventarioBinding(Queue inventarioQueue, TopicExchange ventasExchange) {
        return BindingBuilder.bind(inventarioQueue).to(ventasExchange).with("venta.creada");
    }

    // Binding para asociar la cola del inventario con el intercambio y la clave de ruteo `venta.compensar`
    @Bean
    public Binding inventarioCompensarBinding(Queue inventarioCompensarQueue, TopicExchange ventasExchange) {
        return BindingBuilder.bind(inventarioCompensarQueue).to(ventasExchange).with("venta.compensar");
    }
}
