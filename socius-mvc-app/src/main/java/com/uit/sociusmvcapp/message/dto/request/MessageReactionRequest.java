package com.uit.sociusmvcapp.message.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Request object for adding/removing a message reaction. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageReactionRequest {

  @NotBlank(message = "Message ID is required")
  private String messageId;

  @NotBlank(message = "Reaction is required")
  private String reaction;
}
