package com.uit.sociuscoremodules.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Data Transfer Object for Employee Department information. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeTeamDto {
  private String teamCode;
  private String teamName;
  private String roleCode;
  private String roleName;
  private Boolean isLeader;
}
