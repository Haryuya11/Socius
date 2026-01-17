package com.uit.sociusmvcapp.task.dto.request;

import com.uit.sociusmvcapp.task.enums.TaskPriority;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Request object for creating a new task. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateTaskRequest {
  @NotBlank(message = "Receiver ID is required")
  private String receiverId;

  @NotBlank(message = "Team code is required")
  private String teamCode;

  @NotBlank(message = "Department code is required")
  private String departmentCode;

  @NotBlank(message = "Title is required")
  @Size(max = 200, message = "Title must not exceed 200 characters")
  private String title;

  private String description;
  private Integer deliveryType;
  private TaskPriority priority;

  @NotNull(message = "Start date is required")
  private LocalDate startDate;

  @NotNull(message = "Due date is required")
  @FutureOrPresent(message = "Due date must be today or in the future")
  private LocalDate dueDate;

  private Integer parentId;
}
