package com.uit.sociusmvcapp.message.internal.domain;

import com.uit.sociusmvcapp.shared.domain.BaseEntity;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/** Message entity representing a chat message. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Message extends BaseEntity {
  private String messageId;
  private String conversationId;
  private String senderId;
  private String content;
  private String messageType;
  private String parentMessageId;
  private String metadataJson;
  private Boolean isEdited;
  private LocalDateTime editedAt;
}
