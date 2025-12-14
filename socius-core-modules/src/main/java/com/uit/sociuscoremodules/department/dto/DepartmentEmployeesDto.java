package com.uit.sociuscoremodules.department.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Data Transfer Object for Department Employees information. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentEmployeesDto {
  private String employeeId;
  private String departmentCode;
  private String roleCode;
  private Boolean isPrimary;
}
