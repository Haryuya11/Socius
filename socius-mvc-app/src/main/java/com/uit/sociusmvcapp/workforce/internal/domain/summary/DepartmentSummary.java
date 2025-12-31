package com.uit.sociusmvcapp.workforce.internal.domain.summary;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** DepartmentSummary domain model representing a summary of Department information. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentSummary {
  private String departmentCode;
  private String departmentName;
}
