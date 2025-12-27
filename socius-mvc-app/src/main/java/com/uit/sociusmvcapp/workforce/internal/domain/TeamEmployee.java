package com.uit.sociusmvcapp.workforce.internal.domain;

import com.uit.sociusmvcapp.shared.domain.BaseEntity;
import com.uit.sociusmvcapp.workforce.internal.domain.summary.EmployeeSummary;
import com.uit.sociusmvcapp.workforce.internal.domain.summary.TeamSummary;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * TeamEmployee domain model representing the many-to-many relationship between Team and Employee.
 * It maps to the team_employees table in the database.
 */
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class TeamEmployee extends BaseEntity {
  private String roleCode;
  private Boolean isLeader;

  /** Associated Team summary. */
  private TeamSummary team;

  /** Associated Employee summary. */
  private EmployeeSummary employee;
}
