package com.uit.sociusmvcapp.workforce.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Request to assign an employee to a department. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AssignEmployeeToDepartmentRequest extends AssignEmployeeRequest {
  private Boolean isPrimary;
}
