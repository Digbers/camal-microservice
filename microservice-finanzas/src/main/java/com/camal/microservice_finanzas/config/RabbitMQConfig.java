package com.camal.microservice_finanzas.config;

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
                "com.microservice.ventas.event",           // Paquete de eventos de ventas
                "com.camal.microservice_finanzas.event"    // Paquete de eventos de finanzas
        );

        converter.setJavaTypeMapper(typeMapper);

        return converter;
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
    public TopicExchange finanzasVentasExchange() {
        return new TopicExchange("FinanzasVentasExchange");
    }
    @Bean
    public Queue inventarioVentasActualizadoQueue() {
        return new Queue("InventarioVentasActualizadoQueue");    }

    @Bean
    public Queue ventaCompensarQueue() {
        return new Queue("VentaCompensarQueue");
    }
    @Bean
    public Queue finanzasCobrosErrorQueue() {
        return new Queue("FinanzasCobrosErrorQueue");
    }
    @Bean
    public Queue finanzasCobrosSuccessQueue() {
        return new Queue("FinanzasCobrosSuccessQueue");
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
    public Binding finanzasCobrosErrorBinding(Queue finanzasCobrosErrorQueue, TopicExchange finanzasExchange) {
        return BindingBuilder.bind(finanzasCobrosErrorQueue).to(finanzasExchange).with("finanzas.error-cobros");
    }
    @Bean
    public Binding finanzasCobrosSuccessBinding(Queue finanzasCobrosSuccessQueue, TopicExchange finanzasExchange) {
        return BindingBuilder.bind(finanzasCobrosSuccessQueue).to(finanzasExchange).with("finanzas.success-cobros");
    }
    /*Compras/
     */

    @Bean
    public TopicExchange inventarioCompraExchange() {
        return new TopicExchange("InventarioCompraExchange");
    }
    @Bean
    public TopicExchange finanzasExchange() {
        return new TopicExchange("FinanzasExchange");
    }
    @Bean
    public Queue inventarioActualizadoComprasQueue() {
        return new Queue("InventarioActualizadoComprasQueue");
    }
    @Bean
    public Queue finanzasPagosErrorQueue() {
        return new Queue("FinanzasPagosErrorQueue");
    }
    @Bean
    public Queue finanzasPagosSuccessQueue() {
        return new Queue("FinanzasPagosSuccessQueue");
    }
    @Bean
    public Binding inventarioActualizadoBinding(Queue inventarioActualizadoComprasQueue, TopicExchange inventarioCompraExchange) {
        return BindingBuilder.bind(inventarioActualizadoComprasQueue).to(inventarioCompraExchange).with("inventario.actualizado");
    }

    @Bean
    public Binding finanzasPagosErrorBinding(Queue finanzasPagosErrorQueue, TopicExchange finanzasExchange) {
        return BindingBuilder.bind(finanzasPagosErrorQueue).to(finanzasExchange).with("finanzas.error-pagos");
    }
    @Bean
    public Binding finanzasPagosSuccessBinding(Queue finanzasPagosSuccessQueue, TopicExchange finanzasExchange) {
        return BindingBuilder.bind(finanzasPagosSuccessQueue).to(finanzasExchange).with("finanzas.success-pagos");
    }


}
