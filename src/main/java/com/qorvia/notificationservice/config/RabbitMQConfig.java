package com.qorvia.notificationservice.config;

import com.qorvia.notificationservice.utils.AppConstants;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;

@Configuration
@EnableRabbit
public class RabbitMQConfig {

    // --- Notification Service Async Queue, Exchange, and Routing ---

    @Bean
    public Queue notificationServiceAsyncQueue() {
        return new Queue(AppConstants.NOTIFICATION_SERVICE_ASYNC_QUEUE, true);
    }

    @Bean
    public Exchange notificationServiceAsyncExchange() {
        return new DirectExchange(AppConstants.NOTIFICATION_SERVICE_EXCHANGE, true, false);
    }

    @Bean
    public Binding notificationServiceAsyncBinding() {
        return BindingBuilder
                .bind(notificationServiceAsyncQueue())
                .to(notificationServiceAsyncExchange())
                .with(AppConstants.NOTIFICATION_SERVICE_ROUTING_KEY)
                .noargs();
    }

    // --- Notification Service RPC Queue, Exchange, and Routing ---

    @Bean
    public Queue notificationServiceRpcQueue() {
        return new Queue(AppConstants.NOTIFICATION_SERVICE_RPC_QUEUE, true);
    }

    @Bean
    public Exchange notificationServiceRpcExchange() {
        return new DirectExchange(AppConstants.NOTIFICATION_SERVICE_RPC_EXCHANGE, true, false);
    }

    @Bean
    public Binding notificationServiceRpcBinding() {
        return BindingBuilder
                .bind(notificationServiceRpcQueue())
                .to(notificationServiceRpcExchange())
                .with(AppConstants.NOTIFICATION_SERVICE_RPC_ROUTING_KEY)
                .noargs();
    }

    // Configure the RPC Listener Container for the RPC queues
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrentConsumers(3);
        return factory;
    }
}