package com.uit.sociuscoremodules.teamemployee.request;

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
public class TeamEmployeeSearchCondition {
  private String userId; // email
  private String firstName;
  private String lastName;
  private String roleCode;
}
