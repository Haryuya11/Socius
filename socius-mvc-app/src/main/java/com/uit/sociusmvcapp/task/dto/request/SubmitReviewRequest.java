package com.uit.sociusmvcapp.task.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Request object for submitting a task for review. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubmitReviewRequest {
  @NotBlank(message = "Completion note is required")
  private String completionNote;
}
