package com.uit.sociuscoremodules.team.domain;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Team domain model representing a team entity. It maps to the teams table in the database. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Team {
  private Integer id;
  private String teamCode;
  private String teamName;
  private String departmentCode;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private LocalDateTime deletedAt;
  private Short deleteFlag;
}
