package com.uit.sociusmvcapp.task.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Request object for rejecting a task. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RejectTaskRequest {
  @NotBlank(message = "Rejection reason is required")
  private String rejectionReason;
}
