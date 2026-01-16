package com.uit.sociusmvcapp.task.dto.request;

import com.uit.sociusmvcapp.task.enums.TaskPriority;
import com.uit.sociusmvcapp.task.enums.TaskStatus;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Request object for searching/filtering tasks. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchTaskRequest {
  private String receiverId;
  private String senderId;
  private String teamCode;
  private String departmentCode;
  private List<TaskStatus> status;
  private TaskPriority priority;
  private Integer parentId;
  private Boolean isParent;
  private Boolean overdue;
  private LocalDate dueDateFrom;
  private LocalDate dueDateTo;
  private String search;
}
