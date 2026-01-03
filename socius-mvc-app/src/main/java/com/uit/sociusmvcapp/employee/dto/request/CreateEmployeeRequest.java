package com.uit.sociusmvcapp.employee.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Request object for creating a new user. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateEmployeeRequest {
  private String clientId;
  private String userId;
  private String firstName;
  private String lastName;
  private String systemRole;
  private Long salary;
  private String imageUrl;
}
