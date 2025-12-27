package com.uit.sociusmvcapp.department.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Request object for creating a new department. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateDepartmentRequest {
  private String departmentCode;
  private String departmentName;
}
