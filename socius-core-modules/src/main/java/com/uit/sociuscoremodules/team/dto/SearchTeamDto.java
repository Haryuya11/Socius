package com.uit.sociuscoremodules.team.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Team Data Transfer Object (DTO) representing team information. */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchTeamDto {
  private Integer id;
  private String teamCode;
  private String teamName;
  private String departmentCode;
  private TeamMemberDto teamLead;
  private List<TeamMemberDto> members;
}
