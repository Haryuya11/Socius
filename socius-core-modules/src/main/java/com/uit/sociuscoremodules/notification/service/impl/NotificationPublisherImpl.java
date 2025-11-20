package com.uit.sociuscoremodules.notification.service.impl;

import com.uit.sociuscoremodules.notification.dto.NotificationDto;
import com.uit.sociuscoremodules.notification.service.NotificationPublisher;
import com.uit.sociuscoremodules.shared.constants.MessageConstant;
import com.uit.sociuscoremodules.shared.service.impl.BaseServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** Implementation of NotificationPublisher for publishing notifications to RabbitMQ. */
@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationPublisherImpl extends BaseServiceImpl implements NotificationPublisher {

  /** RabbitTemplate for sending messages to RabbitMQ. */
  private final RabbitTemplate rabbitTemplate;

  /** Exchange name for notifications. */
  @Value("${notification.exchange.name}")
  private String exchangeName;

  /** Routing key for notifications. */
  @Value("${notification.routing.key}")
  private String routingKey;

  /**
   * Publishes a notification.
   *
   * @param notification the notification to be published
   */
  @Override
  public void publishNotification(NotificationDto notification) {
    try {
      rabbitTemplate.convertAndSend(exchangeName, routingKey, notification);
      log.info("Published notification: {}", notification);
    } catch (Exception e) {
      log.error("Failed to publish notification", e);
      throw badRequest(MessageConstant.E_NOTIFY_001);
    }
  }
}
