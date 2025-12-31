package com.uit.sociusmvcapp.employee.internal.domain;

import com.uit.sociusmvcapp.shared.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Employee domain model representing an employee entity. It maps to the employee table in the
 * database.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Employee extends BaseEntity {
  private String clientId;
  private String userId;
  private String firstName;
  private String lastName;
  private String systemRole;
  private String imageUrl;
  private Long salary;
}
