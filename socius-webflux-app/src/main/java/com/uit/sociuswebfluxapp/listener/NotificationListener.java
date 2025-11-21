package com.uit.sociuswebfluxapp.listener;

import com.uit.sociuscoremodules.notification.dto.NotificationDto;
import com.uit.sociuswebfluxapp.websocket.NotificationBroadcaster;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/** Listener for handling notifications from RabbitMQ and broadcasting them to WebSocket clients. */
@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationListener {

  /** Broadcaster for sending notifications to WebSocket clients. */
  private final NotificationBroadcaster broadcaster;

  /**
   * Handles incoming notifications from RabbitMQ.
   *
   * @param notification the notification received from RabbitMQ
   */
  @RabbitListener(queues = "${notification.queue.name}")
  public void handleNotification(NotificationDto notification) {
    log.info("Received notification from RabbitMQ: {}", notification);
    broadcaster.publish(notification);
    log.info("Sent notification to WebSocket clients successfully");
  }
}
