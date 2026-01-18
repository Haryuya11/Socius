package com.uit.sociusmvcapp.message.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Payload for message-related realtime events. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageEventPayload {

  /** The conversation ID. */
  private String conversationId;

  /** The message ID (for MESSAGE_DELETED or references). */
  private String messageId;

  /** The full message data (for NEW_MESSAGE, MESSAGE_UPDATED). */
  private MessageDto message;

  /**
   * Creates a payload for new message or message update events.
   *
   * @param message the message
   * @return the payload
   */
  public static MessageEventPayload forMessage(MessageDto message) {
    return MessageEventPayload.builder()
        .conversationId(message.getConversationId())
        .messageId(message.getMessageId())
        .message(message)
        .build();
  }

  /**
   * Creates a payload for message deletion events.
   *
   * @param conversationId the conversation ID
   * @param messageId the message ID
   * @return the payload
   */
  public static MessageEventPayload forDeletion(String conversationId, String messageId) {
    return MessageEventPayload.builder()
        .conversationId(conversationId)
        .messageId(messageId)
        .build();
  }
}
