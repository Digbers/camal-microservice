package com.camal.microservice_finanzas.config;

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
    public Queue ventasCompensarQueue() {
        return new Queue("ventasCompensarQueue");
    }

    @Bean
    public Binding ventasBinding(Queue ventasQueue, TopicExchange ventasExchange) {
        return BindingBuilder.bind(ventasQueue).to(ventasExchange).with("venta.creada");
    }
    /**
     * Creates a binding between the ventasCompensarQueue and the ventasExchange with a routing key of "venta.compensar".
     *
     * @param  ventasCompensarQueue	the queue to bind to the exchange
     * @param  ventasExchange		the exchange to bind the queue to
     * @return         	a binding between the queue and exchange
     */
    @Bean
    public Binding compensarVentaBinding(Queue ventasCompensarQueue, TopicExchange ventasExchange) {
        return BindingBuilder.bind(ventasCompensarQueue).to(ventasExchange).with("venta.compensar");
    }
}
