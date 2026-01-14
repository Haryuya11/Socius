package com.uit.sociusmvcapp.message.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Request object for creating a new conversation. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateConversationRequest {

  @NotBlank(message = "Conversation type is required")
  private String type;

  private String name;
  private String avatarUrl;

  @NotEmpty(message = "At least one participant is required")
  private List<String> participantIds;
}
