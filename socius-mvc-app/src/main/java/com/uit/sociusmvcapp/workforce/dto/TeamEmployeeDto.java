package com.uit.sociusmvcapp.workforce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** TeamEmployee Data Transfer Object (DTO) representing team-employee relationship information. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TeamEmployeeDto {
  private String roleCode;
  private Boolean isLeader;

  /** Associated Team information. */
  private String teamCode;

  private String teamName;
  private String departmentCode;

  /** Associated Employee information. */
  private String clientId;

  private String userId;
  private String firstName;
  private String lastName;
  private String systemRole;
  private String imageUrl;
  private Long salary;
}
