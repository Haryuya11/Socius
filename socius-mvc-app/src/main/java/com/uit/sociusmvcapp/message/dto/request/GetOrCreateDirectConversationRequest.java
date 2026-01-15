package com.uit.sociusmvcapp.message.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request object for getting or creating a direct conversation. Used for lazy creation of direct
 * conversations between two users.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetOrCreateDirectConversationRequest {

  @NotBlank(message = "Target employee ID is required")
  private String targetEmployeeId;
}
