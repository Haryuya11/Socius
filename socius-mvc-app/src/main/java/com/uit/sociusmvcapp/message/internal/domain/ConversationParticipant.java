package com.uit.sociusmvcapp.message.internal.domain;

import com.uit.sociusmvcapp.shared.domain.BaseEntity;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/** ConversationParticipant entity representing a participant in a conversation. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ConversationParticipant extends BaseEntity {
  private String conversationId;
  private String employeeId;
  private String role;
  private LocalDateTime joinedAt;
  private LocalDateTime leftAt;
  private String lastReadMessageId;
  private LocalDateTime lastReadAt;
  private Boolean isMuted;
  private Boolean isPinned;
}
