package com.uit.sociusmvcapp.task.internal.domain;

import com.uit.sociusmvcapp.shared.domain.BaseEntity;
import com.uit.sociusmvcapp.task.enums.TaskPriority;
import com.uit.sociusmvcapp.task.enums.TaskStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/** Domain model representing a task entity. */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Task extends BaseEntity {
  private Integer parentId;
  private String receiverId;
  private String senderId;
  private String teamCode;
  private String departmentCode;
  private String title;
  private String description;
  private Integer deliveryType;
  private TaskStatus status;
  private TaskPriority priority;
  private LocalDateTime startDate;
  private LocalDateTime dueDate;
}
