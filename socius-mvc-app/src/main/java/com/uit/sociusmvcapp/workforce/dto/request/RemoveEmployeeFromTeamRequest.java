package com.uit.sociusmvcapp.workforce.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Request object for removing an employee from a team. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RemoveEmployeeFromTeamRequest {
  private String employeeId;
}
