package com.uit.sociusmvcapp.workforce.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Request to assign an employee to a workforce. */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public abstract class AssignEmployeeRequest {
  private String employeeId;
  private String roleCode;
}
