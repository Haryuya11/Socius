package com.uit.sociusmvcapp.message.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Data Transfer Object for MessageReaction entity. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageReactionDto {
  @JsonIgnore private Long id;
  private String messageId;
  private String employeeId;
  private String reaction;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;
}
