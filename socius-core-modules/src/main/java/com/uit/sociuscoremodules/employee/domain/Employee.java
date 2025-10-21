package com.uit.sociuscoremodules.employee.domain;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Employee domain model representing an employee entity. It map to the employee table in the
 * database.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Employee {
  private Integer id;
  private String clientId;
  private String userId;
  private String firstName;
  private String lastName;
  private String teamCode;
  private String departmentCode;
  private String roleCode;
  private String imageUrl;
  private Long salary;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private LocalDateTime deletedAt;
  private Short deleteFlag;
}
