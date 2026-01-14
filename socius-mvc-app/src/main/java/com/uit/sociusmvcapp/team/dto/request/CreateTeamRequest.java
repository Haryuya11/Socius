package com.uit.sociusmvcapp.team.dto.request;

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
public class CreateTeamRequest {

  @NotBlank(message = "Team code is required")
  private String teamCode;

  @NotBlank(message = "Team name is required")
  private String teamName;

  @NotBlank(message = "Department code is required")
  private String departmentCode;
}
