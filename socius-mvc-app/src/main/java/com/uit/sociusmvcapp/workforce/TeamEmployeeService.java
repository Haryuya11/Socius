package com.uit.sociusmvcapp.workforce;

import com.uit.sociusmvcapp.workforce.dto.TeamEmployeeBatchResultDto;
import com.uit.sociusmvcapp.workforce.dto.TeamEmployeeDto;
import com.uit.sociusmvcapp.workforce.dto.request.AssignEmployeeToTeamRequest;
import com.uit.sociusmvcapp.workforce.dto.request.TeamEmployeeBatchAddRequest;
import com.uit.sociusmvcapp.workforce.dto.request.TransferTeamEmployeeRequest;

/** Service interface for team-employee relationship operations. */
public interface TeamEmployeeService {

  /**
   * Add an employee to a team.
   *
   * @param teamCode the team code
   * @param request the employee addition request
   * @return the created TeamEmployeeDto
   */
  TeamEmployeeDto addEmployeeToTeam(String teamCode, AssignEmployeeToTeamRequest request);

  /**
   * Batch add employees to a team.
   *
   * @param teamCode the team code
   * @param request the batch addition request
   * @return BatchResultDto containing successful and failed operations
   */
  TeamEmployeeBatchResultDto addEmployeesToTeam(
      String teamCode, TeamEmployeeBatchAddRequest request);

  /**
   * Remove an employee from a team.
   *
   * @param teamCode the team code
   * @param employeeId the employee ID
   */
  void removeEmployeeFromTeam(String teamCode, String employeeId);

  /**
   * Change team lead.
   *
   * @param teamCode the team code
   * @param newLeadEmployeeId the new team lead's employee ID
   * @return the updated TeamEmployeeDto
   */
  TeamEmployeeDto changeTeamLead(String teamCode, String newLeadEmployeeId);

  /**
   * Transfer an employee from one team to another.
   *
   * @param request the request containing transfer details
   */
  void transferEmployee(TransferTeamEmployeeRequest request);
}
