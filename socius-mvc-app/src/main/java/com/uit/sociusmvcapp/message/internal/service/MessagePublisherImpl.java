package com.uit.sociusmvcapp.message.internal.service;

import com.uit.sociusmvcapp.message.MessagePublisher;
import com.uit.sociusmvcapp.message.dto.MessageDto;
import com.uit.sociusmvcapp.message.dto.RealtimeMessageEvent;
import com.uit.sociusmvcapp.shared.constants.MessageConstant;
import com.uit.sociusmvcapp.shared.service.ExceptionFactory;
import java.time.LocalDateTime;
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

  @Value("${message.routing.key:message.realtime}")
  private String routingKey;

  @Override
  public void publishNewMessage(MessageDto message) {
    RealtimeMessageEvent event =
        RealtimeMessageEvent.builder()
            .eventType("NEW_MESSAGE")
            .conversationId(message.getConversationId())
            .message(message)
            .timestamp(LocalDateTime.now())
            .build();
    publish(event);
  }

  @Override
  public void publishMessageUpdated(MessageDto message) {
    RealtimeMessageEvent event =
        RealtimeMessageEvent.builder()
            .eventType("MESSAGE_UPDATED")
            .conversationId(message.getConversationId())
            .message(message)
            .timestamp(LocalDateTime.now())
            .build();
    publish(event);
  }

  @Override
  public void publishMessageDeleted(String conversationId, String messageId) {
    RealtimeMessageEvent event =
        RealtimeMessageEvent.builder()
            .eventType("MESSAGE_DELETED")
            .conversationId(conversationId)
            .messageId(messageId)
            .timestamp(LocalDateTime.now())
            .build();
    publish(event);
  }

  @Override
  public void publishTypingIndicator(String conversationId, String employeeId, boolean isTyping) {
    RealtimeMessageEvent event =
        RealtimeMessageEvent.builder()
            .eventType("TYPING_INDICATOR")
            .conversationId(conversationId)
            .employeeId(employeeId)
            .isTyping(isTyping)
            .timestamp(LocalDateTime.now())
            .build();
    publish(event);
  }

  private void publish(RealtimeMessageEvent event) {
    try {
      rabbitTemplate.convertAndSend(exchangeName, routingKey, event);
      log.info("Published message event: {}", event.getEventType());
    } catch (Exception e) {
      log.error("Failed to publish message event", e);
      throw ExceptionFactory.badRequest(MessageConstant.E_MSG_001);
    }
  }
}
