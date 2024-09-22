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
        return new TopicExchange("ventasExchange");
    }

    @Bean
    public Queue ventasQueue() {
        return new Queue("ventasQueue");
    }

    @Bean
    public Binding ventasBinding(Queue ventasQueue, TopicExchange ventasExchange) {
        return BindingBuilder.bind(ventasQueue).to(ventasExchange).with("venta.#");
    }
    @Bean
    public Queue ventasCompensarQueue() {
        return new Queue("ventasCompensarQueue");
    }

    // Binding para asociar la cola de compensación con el intercambio y la clave de ruteo `venta.compensar`
    @Bean
    public Binding compensarVentaBinding(Queue ventasCompensarQueue, TopicExchange ventasExchange) {
        return BindingBuilder.bind(ventasCompensarQueue).to(ventasExchange).with("venta.compensar");
    }
}
