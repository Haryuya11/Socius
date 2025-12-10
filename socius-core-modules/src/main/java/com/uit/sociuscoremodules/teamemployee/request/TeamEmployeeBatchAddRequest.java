package com.uit.sociuscoremodules.teamemployee.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.ArrayList;
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
public class TeamEmployeeBatchAddRequest {

  @NotEmpty(message = "Employees list cannot be empty")
  @Valid
  private List<TeamEmployeeAddRequest> employees;

  /**
   * Constructor for single employee addition.
   *
   * @param singleEmployee the single employee to add
   */
  public TeamEmployeeBatchAddRequest(TeamEmployeeAddRequest singleEmployee) {
    this.employees = new ArrayList<>();
    this.employees.add(singleEmployee);
  }

  /**
   * Check if this is a single employee addition.
   *
   * @return true if only one employee, false otherwise
   */
  public boolean isSingleEmployee() {
    return employees != null && employees.size() == 1;
  }
}
