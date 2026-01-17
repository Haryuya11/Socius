package com.uit.sociusmvcapp.task.internal.domain;

import com.uit.sociusmvcapp.task.enums.ActivityType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Domain model representing a task activity (audit trail). */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskActivity {
  private Integer id;
  private Integer taskId;
  private ActivityType activityType;
  private String actorId;
  private String note;
  private LocalDateTime createdAt;
}
