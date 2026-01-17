package com.uit.sociusmvcapp.message.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Payload for typing indicator events. Separated from MessageEventPayload as per requirements. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TypingIndicatorPayload {

  /** The conversation ID. */
  private String conversationId;

  /** The employee ID of the user who is typing. */
  private String employeeId;

  /** Whether the user is currently typing. */
  private Boolean isTyping;

  /**
   * Creates a typing indicator payload.
   *
   * @param conversationId the conversation ID
   * @param employeeId the employee ID
   * @param isTyping whether the user is typing
   * @return the payload
   */
  public static TypingIndicatorPayload of(
      String conversationId, String employeeId, boolean isTyping) {
    return TypingIndicatorPayload.builder()
        .conversationId(conversationId)
        .employeeId(employeeId)
        .isTyping(isTyping)
        .build();
  }
}
