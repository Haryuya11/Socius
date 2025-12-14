package com.uit.sociuscoremodules.teamemployee.dto;

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
  private Integer id;
  private String teamCode;
  private String departmentCode;
  private String employeeId;
  private String roleCode;
  private Boolean isLeader;
  private String firstName;
  private String lastName;
  private String imageUrl;
}
