package com.uit.sociusmvcapp.task.internal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Context object containing parent task information for sub-task creation. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ParentTaskContext {
  private Integer parentId;
  private String teamCode;
  private String departmentCode;
}
