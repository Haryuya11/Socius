package com.uit.sociusmvcapp.employee.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request object for updating employee salary.
 *
 * <p>This DTO is intentionally separated from UpdateEmployeeRequest to enforce proper authorization
 * checks. Only users with 'employee.salary.update' permission can update salary.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateSalaryRequest {
  private Long salary;
}
