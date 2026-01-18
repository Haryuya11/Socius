package com.uit.sociusmvcapp.department.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Data transfer object for searching departments. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchDepartmentDto {
  private String departmentCode;
  private String departmentName;
}
