package com.uit.sociuscoremodules.department.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Request object for adding an employee to a department. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeAddRequest {
  private String roleCode;
  private Boolean isPrimary;
}
