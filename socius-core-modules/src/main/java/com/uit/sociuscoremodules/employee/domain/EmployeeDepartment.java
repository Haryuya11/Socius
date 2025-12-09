package com.uit.sociuscoremodules.employee.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * EmployeeDepartment domain model representing the association between an employee and a
 * department.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDepartment {
  private String departmentCode;
  private String departmentName;
  private String roleCode;
  private String roleName;
  private Boolean isPrimary;
}
