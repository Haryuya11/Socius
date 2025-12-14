package com.uit.sociuscoremodules.department.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Request object for changing an employee's role within a department. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeAddManyRequest {
  private String employeeId;
  private String roleCode;
  private Boolean isPrimary;
}
