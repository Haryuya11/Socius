package com.uit.sociusmvcapp.message.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Data Transfer Object for real-time message events sent to Ktor. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RealtimeMessageEvent {
  private String eventType;
  private String conversationId;
  private MessageDto message;
  private String messageId;
  private String employeeId;
  private Boolean isTyping;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime timestamp;
}
