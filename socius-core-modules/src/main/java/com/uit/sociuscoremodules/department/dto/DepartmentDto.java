package com.uit.sociuscoremodules.department.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Department Data Transfer Object (DTO) representing department information. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentDto {
  private String departmentCode;
  private String departmentName;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private LocalDateTime deletedAt;
  private Short deleteFlag;
}
