package com.uit.sociuscoremodules.teamemployee.domain;

import com.uit.sociuscoremodules.employee.domain.Employee;
import com.uit.sociuscoremodules.team.domain.Team;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * TeamEmployee domain model representing the many-to-many relationship between Team and Employee.
 * It maps to the team_employees table in the database.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeamEmployee {
  private Integer id;
  private String employeeId;
  private String teamCode;
  private String roleCode;
  private Boolean isLeader;
  private Employee employeeInfo;
  private Team teamInfo;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private LocalDateTime deletedAt;
  private Short deleteFlag;
}
