package com.uit.sociusmvcapp.task.dto;

import com.uit.sociusmvcapp.task.enums.TaskPriority;
import com.uit.sociusmvcapp.task.enums.TaskStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** DTO for task information. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskDto {
  private Integer id;
  private Integer parentId;
  private String receiverId;
  private String receiverName;
  private String senderId;
  private String senderName;
  private String teamCode;
  private String departmentCode;
  private String title;
  private String description;
  private Integer deliveryType;
  private TaskStatus status;
  private TaskPriority priority;
  private LocalDateTime startDate;
  private LocalDateTime dueDate;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private Integer subTaskCount;
}
