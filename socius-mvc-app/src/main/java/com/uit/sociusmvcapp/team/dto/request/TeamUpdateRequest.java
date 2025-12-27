package com.uit.sociusmvcapp.team.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request object for updating team information. Team lead changes should use separate Change Team
 * Lead API.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TeamUpdateRequest {
  private String teamName;
  private String departmentCode;
}
