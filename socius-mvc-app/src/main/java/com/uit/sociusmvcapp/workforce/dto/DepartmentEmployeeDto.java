package com.uit.sociusmvcapp.workforce.dto;

import com.uit.sociusmvcapp.workforce.internal.domain.summary.DepartmentSummary;
import com.uit.sociusmvcapp.workforce.internal.domain.summary.EmployeeSummary;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Data Transfer Object for Department Employees information. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentEmployeeDto {
  private String roleCode;
  private Boolean isPrimary;

  /** Associated Department summary. */
  private DepartmentSummary department;

  /** Associated Employee summary. */
  private EmployeeSummary employee;
}
