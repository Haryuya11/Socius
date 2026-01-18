package com.uit.sociusmvcapp.message.internal.service;

import com.uit.sociusmvcapp.message.MessagePublisher;
import com.uit.sociusmvcapp.message.dto.MessageDto;
import com.uit.sociusmvcapp.message.dto.MessageEventPayload;
import com.uit.sociusmvcapp.message.dto.TypingIndicatorPayload;
import com.uit.sociusmvcapp.shared.constants.MessageConstant;
import com.uit.sociusmvcapp.shared.enums.RealtimeEventType;
import com.uit.sociusmvcapp.shared.event.RealtimeEvent;
import com.uit.sociusmvcapp.shared.service.ExceptionFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** Implementation of MessagePublisher for publishing messages to RabbitMQ. */
@Service
@Slf4j
@RequiredArgsConstructor
public class MessagePublisherImpl implements MessagePublisher {

  private final RabbitTemplate rabbitTemplate;

  @Value("${rabbitmq.exchange.name}")
  private String exchangeName;

  @Value("${message.routing.key}")
  private String routingKey;

  @Override
  public void publishNewMessage(MessageDto message, List<String> targetUserIds) {
    MessageEventPayload payload = MessageEventPayload.forMessage(message);
    RealtimeEvent<MessageEventPayload> event =
        RealtimeEvent.message(RealtimeEventType.NEW_MESSAGE, payload, targetUserIds);
    publish(event);
  }

  @Override
  public void publishMessageUpdated(MessageDto message, List<String> targetUserIds) {
    MessageEventPayload payload = MessageEventPayload.forMessage(message);
    RealtimeEvent<MessageEventPayload> event =
        RealtimeEvent.message(RealtimeEventType.MESSAGE_UPDATED, payload, targetUserIds);
    publish(event);
  }

  @Override
  public void publishMessageDeleted(
      String conversationId, String messageId, List<String> targetUserIds) {
    MessageEventPayload payload = MessageEventPayload.forDeletion(conversationId, messageId);
    RealtimeEvent<MessageEventPayload> event =
        RealtimeEvent.message(RealtimeEventType.MESSAGE_DELETED, payload, targetUserIds);
    publish(event);
  }

  @Override
  public void publishTypingIndicator(
      String conversationId, String employeeId, boolean isTyping, List<String> targetUserIds) {
    TypingIndicatorPayload payload =
        TypingIndicatorPayload.of(conversationId, employeeId, isTyping);
    RealtimeEvent<TypingIndicatorPayload> event =
        RealtimeEvent.message(RealtimeEventType.TYPING_INDICATOR, payload, targetUserIds);
    publish(event);
  }

  /**
   * Publish a realtime event to RabbitMQ.
   *
   * @param event the event to be published
   */
  private void publish(RealtimeEvent<?> event) {
    try {
      rabbitTemplate.convertAndSend(exchangeName, routingKey, event);
      log.info("Published message event: {}", event.getEventType().getCode());
    } catch (Exception e) {
      log.error("Failed to publish message event", e);
      throw ExceptionFactory.badRequest(MessageConstant.E_MSG_001);
    }
  }
}
