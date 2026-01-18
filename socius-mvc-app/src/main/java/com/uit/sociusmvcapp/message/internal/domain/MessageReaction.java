package com.uit.sociusmvcapp.message.internal.domain;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** MessageReaction entity representing a reaction to a message. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageReaction {
  private Long id;
  private String messageId;
  private String employeeId;
  private String reaction;
  private LocalDateTime createdAt;
  private Short deleteFlag;
}
