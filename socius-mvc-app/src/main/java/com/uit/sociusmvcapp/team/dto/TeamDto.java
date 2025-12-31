package com.uit.sociusmvcapp.team.dto;

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
public class TeamDto {
  private String teamCode;
  private String teamName;
  private String departmentCode;
}
