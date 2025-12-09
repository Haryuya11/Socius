package com.uit.sociuscoremodules.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Data Transfer Object for Employee Department information. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDepartmentDto {
  private String departmentCode;
  private String departmentName;
  private String roleCode;
  private String roleName;
  private Boolean isPrimary;
}
