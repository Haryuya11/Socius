package com.uit.sociusmvcapp.department.internal.domain;

import com.uit.sociusmvcapp.shared.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Department domain model representing a department entity. It maps to the department table in the
 * database.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Department extends BaseEntity {
  private String departmentCode;
  private String departmentName;
}
