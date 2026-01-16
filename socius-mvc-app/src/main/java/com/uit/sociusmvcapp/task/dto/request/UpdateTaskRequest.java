package com.uit.sociusmvcapp.task.dto.request;

import com.uit.sociusmvcapp.task.enums.TaskPriority;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Request object for updating task information. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTaskRequest {
  private String receiverId;

  @Size(max = 200, message = "Title must not exceed 200 characters")
  private String title;

  private String description;
  private Integer deliveryType;
  private TaskPriority priority;
  private LocalDate startDate;
  private LocalDate dueDate;
}
