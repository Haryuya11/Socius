package com.uit.sociusmvcapp.department.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Request object for updating department information. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateDepartmentRequest {
  private String departmentName;
}
