package com.uit.sociusmvcapp.message.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Request object for updating a message. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMessageRequest {

  @NotBlank(message = "Message content is required")
  private String content;
}
