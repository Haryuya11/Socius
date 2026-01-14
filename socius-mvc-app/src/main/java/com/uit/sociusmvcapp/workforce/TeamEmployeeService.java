package com.uit.sociusmvcapp.workforce;

import com.uit.sociusmvcapp.workforce.dto.TeamEmployeeBatchResultDto;
import com.uit.sociusmvcapp.workforce.dto.TeamEmployeeDto;
import com.uit.sociusmvcapp.workforce.dto.TeamEmployeeRemovalResultDto;
import com.uit.sociusmvcapp.workforce.dto.request.AddEmployeesToTeamRequest;
import com.uit.sociusmvcapp.workforce.dto.request.RemoveEmployeesFromTeamRequest;
import com.uit.sociusmvcapp.workforce.dto.request.TransferTeamEmployeeRequest;
import java.util.List;

/** Service interface for team-employee relationship operations. */
public interface TeamEmployeeService {

  /**
   * Get all employees in a team.
   *
   * @param teamCode the team code
   * @return list of TeamEmployeeDto
   */
  List<TeamEmployeeDto> getEmployeesByTeamCode(String teamCode);

  /**
   * Add employees to a team (supports both single and batch).
   *
   * @param teamCode the team code
   * @param request the batch addition request (can contain single or multiple employees)
   * @return BatchResultDto containing successful and failed operations
   */
  TeamEmployeeBatchResultDto addEmployeesToTeam(String teamCode, AddEmployeesToTeamRequest request);

  /**
   * Remove employees from a team (supports both single and batch).
   *
   * @param teamCode the team code
   * @param request the batch removal request (can contain single or multiple employees)
   * @return TeamEmployeeRemovalResultDto containing removed IDs and failed operations
   */
  TeamEmployeeRemovalResultDto removeEmployeesFromTeam(
      String teamCode, RemoveEmployeesFromTeamRequest request);

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
