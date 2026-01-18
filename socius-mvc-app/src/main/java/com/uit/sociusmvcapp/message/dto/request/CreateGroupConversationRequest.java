package com.uit.sociusmvcapp.message.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Request object for creating a new group conversation. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateGroupConversationRequest {

  @NotBlank(message = "Group name is required")
  private String name;

  private String avatarUrl;

  @NotEmpty(message = "At least one participant is required")
  @Size(min = 1, message = "Group must have at least one other participant")
  private List<String> participantIds;
}
