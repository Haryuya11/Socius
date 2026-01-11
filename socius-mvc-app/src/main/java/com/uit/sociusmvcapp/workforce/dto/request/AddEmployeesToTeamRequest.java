package com.uit.sociusmvcapp.workforce.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request object for adding employees to a team. Supports both single and multiple employee
 * additions.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddEmployeesToTeamRequest {

  @NotEmpty(message = "Employees list cannot be empty")
  @Valid
  private List<AddEmployeeToTeamRequest> employees;
}
