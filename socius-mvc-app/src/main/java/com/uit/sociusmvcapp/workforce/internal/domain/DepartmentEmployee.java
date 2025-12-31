package com.uit.sociusmvcapp.workforce.internal.domain;

import com.uit.sociusmvcapp.shared.domain.BaseEntity;
import com.uit.sociusmvcapp.workforce.internal.domain.summary.DepartmentSummary;
import com.uit.sociusmvcapp.workforce.internal.domain.summary.EmployeeSummary;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * DepartmentEmployees domain model representing the association between employees and departments.
 * It maps to the department_employees table in the database.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class DepartmentEmployee extends BaseEntity {
  private String roleCode;
  private Boolean isPrimary;

  /** Associated Department summary. */
  private DepartmentSummary department;

  /** Associated Employee summary. */
  private EmployeeSummary employee;
}
