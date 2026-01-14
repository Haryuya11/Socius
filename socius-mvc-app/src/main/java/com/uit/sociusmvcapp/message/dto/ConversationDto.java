package com.uit.sociusmvcapp.message.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Data Transfer Object for Conversation entity. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConversationDto {
  private Long id;
  private String conversationId;
  private String type;
  private String name;
  private String avatarUrl;
  private String createdBy;
  private Long lastMessageId;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime lastMessageAt;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;
}
