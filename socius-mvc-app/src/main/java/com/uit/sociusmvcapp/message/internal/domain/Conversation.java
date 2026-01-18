package com.uit.sociusmvcapp.message.internal.domain;

import com.uit.sociusmvcapp.shared.domain.BaseEntity;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/** Conversation entity representing a chat conversation. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Conversation extends BaseEntity {
  private String conversationId;
  private String type;
  private String name;
  private String avatarUrl;
  private String createdBy;
  private String lastMessageId;
  private LocalDateTime lastMessageAt;
}
