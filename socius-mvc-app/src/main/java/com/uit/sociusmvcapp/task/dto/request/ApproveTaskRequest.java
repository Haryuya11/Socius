package com.uit.sociusmvcapp.task.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Request object for approving a task. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApproveTaskRequest {
  @NotBlank(message = "Review note is required")
  private String reviewNote;
}
