package com.microservice.ventas.config;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class RabbitMQConfig {
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory("localhost");
        // Aquí puedes agregar más configuraciones de conexión si las necesitas
        return factory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        // Crear primero el ObjectMapper y configurarlo
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(new JavaTimeModule());
        // Crear el conversor pasándole el ObjectMapper
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);

        // Configurar el type mapper
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.setTrustedPackages("*");

        // Asignar el type mapper
        converter.setJavaTypeMapper(typeMapper);

        return converter;
    }
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jackson2JsonMessageConverter());

        // Configurar el manejo de errores si es necesario
        factory.setErrorHandler(throwable -> {
            log.error("Error en el procesamiento del mensaje: ", throwable);
        });

        return factory;
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
    public TopicExchange finanzasVentasExchange() {
        return new TopicExchange("FinanzasVentasExchange");
    }
    @Bean
    public TopicExchange compensacionVentasExchange() {
        return new TopicExchange("CompensacionVentasExchange");
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
    public Queue ventaCreadaQueue() {
        return new Queue("VentaCreadaQueue");
    }
    @Bean
    public Queue ventaCompensarQueue() {
        return new Queue("VentaCompensarQueue");
    }
    @Bean
    public Queue finanzasCobrosErrorQueue() {
        return QueueBuilder.durable("FinanzasCobrosErrorQueue")
                .withArgument("x-dead-letter-exchange", "DeadLetterExchange")
                .withArgument("x-dead-letter-routing-key", "dead-letter")
                .build();
    }
    @Bean
    public Queue finanzasCobrosSuccessQueue() {
        return new Queue("FinanzasCobrosSuccessQueue");
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
    @Bean
    public Binding finanzasCobrosSuccessBinding(Queue finanzasCobrosSuccessQueue, TopicExchange finanzasVentasExchange) {
        return BindingBuilder.bind(finanzasCobrosSuccessQueue).to(finanzasVentasExchange).with("finanzas.success-cobros");
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
    public TopicExchange finanzasExchange() {
        return new TopicExchange("FinanzasExchange");
    }
    @Bean
    public Queue compraCreadaQueue() {
        return new Queue("CompraCreadaQueue");
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
    public Queue finanzasPagosErrorQueue() {
        return new Queue("FinanzasPagosErrorQueue");
    }
    @Bean
    public Queue finanzasPagosSuccessQueue() {
        return new Queue("FinanzasPagosSuccessQueue");
    }

    @Bean
    public Binding compraCreadaBinding(Queue compraCreadaQueue, TopicExchange compraExchange) {
        return BindingBuilder.bind(compraCreadaQueue).to(compraExchange).with("compra.creada");
    }
    @Bean
    public Binding compensacionBinding(Queue compraCompensarQueue, TopicExchange compensacionCompraExchange) {
        return BindingBuilder.bind(compraCompensarQueue).to(compensacionCompraExchange).with("compra.compensar");
    }
    @Bean
    public Binding inventarioErrorActualizandoBinding(Queue errorActualizandoComprasQueue, TopicExchange inventarioCompraExchange) {
        return BindingBuilder.bind(errorActualizandoComprasQueue).to(inventarioCompraExchange).with("inventario.error-actualizando-c");
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
