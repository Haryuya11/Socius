package com.uit.sociuscoremodules.employee.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** EmployeeTeam domain model representing the association between an employee and a team. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeTeam {
  private String teamCode;
  private String teamName;
  private String roleCode;
  private String roleName;
  private Boolean isLeader;
}
