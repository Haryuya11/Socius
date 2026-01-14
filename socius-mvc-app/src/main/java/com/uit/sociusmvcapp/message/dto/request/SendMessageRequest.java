package com.uit.sociusmvcapp.message.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Request object for sending a message. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SendMessageRequest {

  @NotBlank(message = "Conversation ID is required")
  private String conversationId;

  @NotBlank(message = "Message content is required")
  private String content;

  @NotBlank(message = "Message type is required")
  private String messageType;

  private String parentMessageId;
  private Object metadata;
}
