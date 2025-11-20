package com.uit.sociuswebfluxapp.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** RabbitMqConfig configures RabbitMQ settings for the application, including queue definitions. */
@Configuration
public class RabbitMqConfig {

  /** Exchange name for notifications. */
  @Value("${notification.exchange.name}")
  private String exchangeName;

  /** Queue name for notifications. */
  @Value("${notification.queue.name}")
  private String queueName;

  /** Routing key for notifications. */
  @Value("${notification.routing.key}")
  private String routingKey;

  /**
   * Defines the notification queue.
   *
   * @return the notification Queue
   */
  @Bean
  public Queue notificationQueue() {
    return new Queue(queueName, true);
  }

  /**
   * Defines the notification exchange.
   *
   * @return the notification TopicExchange
   */
  @Bean
  TopicExchange notificationExchange() {
    return new TopicExchange(exchangeName);
  }

  /**
   * Binds the queue to the exchange with the specified routing key.
   *
   * @param queue the notification Queue
   * @param exchange the notification TopicExchange
   * @return the Binding between the queue and exchange
   */
  @Bean
  public Binding binding(Queue queue, TopicExchange exchange) {
    return BindingBuilder.bind(queue).to(exchange).with(routingKey);
  }

  /**
   * Configures the message converter to use Jackson for JSON serialization.
   *
   * @return the Jackson2JsonMessageConverter
   */
  @Bean
  public Jackson2JsonMessageConverter messageConverter() {
    return new Jackson2JsonMessageConverter();
  }

  /**
   * Configures the RabbitTemplate with the custom message converter.
   *
   * @param connectionFactory the ConnectionFactory for RabbitMQ
   * @return the configured RabbitTemplate
   */
  @Bean
  public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
    RabbitTemplate template = new RabbitTemplate(connectionFactory);
    template.setMessageConverter(messageConverter());
    return template;
  }
}
