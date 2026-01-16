package com.uit.sociusmvcapp.task.dto;

import com.uit.sociusmvcapp.task.enums.ActivityType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** DTO for task activity information. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskActivityDto {
  private Integer id;
  private Integer taskId;
  private ActivityType activityType;
  private String actorId;
  private String actorName;
  private String note;
  private LocalDateTime createdAt;
}
