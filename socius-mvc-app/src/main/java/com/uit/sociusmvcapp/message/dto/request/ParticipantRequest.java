package com.uit.sociusmvcapp.message.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Request object for adding a participant to a conversation. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParticipantRequest {

  @NotBlank(message = "Employee ID is required")
  private String employeeId;

  private String role;
}
