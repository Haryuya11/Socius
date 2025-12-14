package com.uit.sociuscoremodules.teamemployee.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Request object for adding an employee to a team. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TeamEmployeeAddRequest {
  @NotBlank(message = "Client ID is required")
  private String employeeId;

  private String roleCode;
  private Boolean isLeader;
}
