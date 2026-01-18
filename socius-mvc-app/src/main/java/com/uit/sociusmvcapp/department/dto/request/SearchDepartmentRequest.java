package com.uit.sociusmvcapp.department.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Search criteria for departments. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchDepartmentRequest {
  private String departmentCode;
  private String departmentName;
}
