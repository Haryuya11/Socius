package com.uit.sociuscoremodules.employee.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Request object for creating a new user. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeCreateRequest {
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
