package com.uit.sociusmvcapp.task.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Request object for cancelling a task. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CancelTaskRequest {
  @NotBlank(message = "Cancellation reason is required")
  private String cancellationReason;
}
