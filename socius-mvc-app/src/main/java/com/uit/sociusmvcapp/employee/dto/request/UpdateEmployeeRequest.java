package com.uit.sociusmvcapp.employee.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request object for updating employee profile information.
 *
 * <p>Note: This DTO intentionally excludes salary, clientId, and userId fields to prevent mass
 * assignment vulnerabilities. Salary updates should use the dedicated UpdateSalaryRequest endpoint.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEmployeeRequest {
  private String firstName;
  private String lastName;
  private String imageUrl;
}
