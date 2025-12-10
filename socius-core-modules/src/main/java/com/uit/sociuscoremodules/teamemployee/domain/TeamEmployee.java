package com.uit.sociuscoremodules.teamemployee.domain;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * TeamEmployee domain model representing the many-to-many relationship between Team and Employee.
 * It maps to the team_employees table in the database.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TeamEmployee {
  private Integer id;
  private String employeeId; // employee's user_id from employees table
  private String teamCode; // team's team_code from teams table
  private String roleCode; // Functional role code from roles table
  private Boolean isLeader; // Team leadership flag
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private LocalDateTime deletedAt;
  private Short deleteFlag;
}
