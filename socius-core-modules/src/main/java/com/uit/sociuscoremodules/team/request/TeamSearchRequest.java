package com.uit.sociuscoremodules.team.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Search criteria for teams. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TeamSearchRequest {
  private String teamCode;
  private String teamName;
  private String departmentCode;
}
