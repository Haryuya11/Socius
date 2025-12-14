package com.uit.sociuscoremodules.department.domain;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Department domain model representing a department entity. It maps to the department table in the
 * database.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Department {
  private Integer id;
  private String departmentCode;
  private String departmentName;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private LocalDateTime deletedAt;
  private Short deleteFlag;
}
