package com.uit.sociuscoremodules.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Employee Data Transfer Object (DTO) representing employee information. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDto {
  private String clientId;
  private String userId;
  private String firstName;
  private String lastName;
  private String teamCode;
  private String departmentCode;
  private String roleCode;
  private Long salary;
  private String imageUrl;
}
