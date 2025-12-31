package com.uit.sociusmvcapp.workforce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** Search condition for team employees. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SearchTeamEmployeeDto {
  private String teamCode;
  private String employeeId;
  private String firstName;
  private String lastName;
  private String roleCode;
}
