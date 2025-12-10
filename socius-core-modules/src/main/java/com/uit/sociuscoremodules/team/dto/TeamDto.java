package com.uit.sociuscoremodules.team.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Team Data Transfer Object (DTO) representing team information. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TeamDto {
  private Integer id;
  private String teamCode;
  private String teamName;
  private String departmentCode;
  private TeamMemberDto teamLead;
  private List<TeamMemberDto> members;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
