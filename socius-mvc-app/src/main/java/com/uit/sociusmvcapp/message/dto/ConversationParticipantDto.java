package com.uit.sociusmvcapp.message.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Data Transfer Object for ConversationParticipant entity. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConversationParticipantDto {
  private Long id;
  private String conversationId;
  private String employeeId;
  private String role;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime joinedAt;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime leftAt;

  private Long lastReadMessageId;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime lastReadAt;

  private Boolean isMuted;
  private Boolean isPinned;
}
