package com.uit.sociusmvcapp.config;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** RabbitMqConfig configures RabbitMQ settings for the MVC application. */
@Configuration
public class RabbitMqConfig {

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
