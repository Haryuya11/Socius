package com.uit.sociuscoremodules.team.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Data transfer object for searching teams. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchTeamDto {
  private Long id;
  private String teamCode;
  private String teamName;
  private String departmentCode;
  private TeamMemberDto teamLead;
}
