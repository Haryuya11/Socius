package com.uit.sociuscoremodules.team.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Request object for creating a new team. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TeamCreateRequest {

  @NotBlank(message = "Team code is required")
  private String teamCode;

  @NotBlank(message = "Team name is required")
  private String teamName;

  private String departmentCode;

  @NotBlank(message = "Team lead client ID is required")
  private String teamLeadClientId;
}
