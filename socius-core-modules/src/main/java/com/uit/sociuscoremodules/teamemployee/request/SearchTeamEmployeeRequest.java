package com.uit.sociuscoremodules.teamemployee.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** Request object for searching team employees criteria. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SearchTeamEmployeeRequest {
  private String teamCode;
  private String employeeId;
  private String userId; // email
  private String firstName;
  private String lastName;
  private String roleCode;
}
