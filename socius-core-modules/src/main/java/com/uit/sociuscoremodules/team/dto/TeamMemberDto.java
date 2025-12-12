package com.uit.sociuscoremodules.team.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** DTO for team member information in team responses. */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamMemberDto {
  private String clientId;
  private String userId;
  private String firstName;
  private String lastName;
  private String imageUrl;
}
