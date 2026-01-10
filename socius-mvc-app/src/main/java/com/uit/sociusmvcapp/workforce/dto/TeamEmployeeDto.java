package com.uit.sociusmvcapp.workforce.dto;

import com.uit.sociusmvcapp.workforce.internal.domain.summary.EmployeeSummary;
import com.uit.sociusmvcapp.workforce.internal.domain.summary.TeamSummary;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** TeamEmployee Data Transfer Object (DTO) representing team-employee relationship information. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TeamEmployeeDto {
  private String roleCode;
  private Boolean isLeader;

  /** Associated Team summary. */
  private TeamSummary team;

  /** Associated Employee summary. */
  private EmployeeSummary employee;
}
