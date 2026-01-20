package com.uit.sociusmvcapp.task.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Request object for reopening a task. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReopenTaskRequest {
  @NotBlank(message = "Reopen reason is required")
  private String reopenReason;
}
