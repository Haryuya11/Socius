package com.uit.sociusmvcapp.workforce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Data Transfer Object for Department Employees information. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentEmployeeDto {
  private String roleCode;
  private Boolean isPrimary;

  /** Associated Department information. */
  private String departmentCode;

  private String departmentName;

  /** Associated Employee information. */
  private String clientId;

  private String userId;
  private String firstName;
  private String lastName;
  private String systemRole;
  private String imageUrl;
  private Long salary;
}
