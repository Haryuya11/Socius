package com.uit.sociusmvcapp.workforce.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Request object for adding an employee to a team. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddEmployeeToTeamRequest extends AssignEmployeeRequest {
  private Boolean isLeader;
}
