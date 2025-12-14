package com.uit.sociuscoremodules.department.domain;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DepartmentEmployees domain model representing the association between employees and departments.
 * It maps to the department_employees table in the database.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentEmployees {
  private Integer id;
  private String employeeId;
  private String departmentCode;
  private String roleCode;
  private Boolean isPrimary;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private LocalDateTime deletedAt;
  private Short deleteFlag;
}
