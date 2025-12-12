package com.uit.sociuscoremodules.employee.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Employee domain model representing an employee entity. It maps to the employee table in the
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
  private String systemRole;
  private String imageUrl;
  private Long salary;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private LocalDateTime deletedAt;
  private Short deleteFlag;

  private List<EmployeeDepartment> departments;
  private List<EmployeeTeam> teams;
  private Set<Permission> permissions;
}
