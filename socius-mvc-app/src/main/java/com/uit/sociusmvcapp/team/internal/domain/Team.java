package com.uit.sociusmvcapp.team.internal.domain;

import com.uit.sociusmvcapp.shared.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/** Team domain model representing a team entity. It maps to the teams table in the database. */
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Team extends BaseEntity {
  private String teamCode;
  private String teamName;
  private String departmentCode;
}
