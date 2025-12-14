package com.uit.sociuscoremodules.teamemployee.service;

import com.uit.sociuscoremodules.employee.dto.EmployeeDto;
import com.uit.sociuscoremodules.shared.request.PaginationSearchRequest;
import com.uit.sociuscoremodules.shared.response.PageResponse;
import com.uit.sociuscoremodules.teamemployee.dto.SearchTeamEmployeeDto;
import com.uit.sociuscoremodules.teamemployee.dto.TeamEmployeeBatchResultDto;
import com.uit.sociuscoremodules.teamemployee.dto.TeamEmployeeDto;
import com.uit.sociuscoremodules.teamemployee.request.SearchTeamEmployeeRequest;
import com.uit.sociuscoremodules.teamemployee.request.TeamEmployeeAddRequest;
import com.uit.sociuscoremodules.teamemployee.request.TeamEmployeeBatchAddRequest;
import com.uit.sociuscoremodules.teamemployee.request.TransferTeamEmployeeRequest;
import java.util.List;
import java.util.Map;

/** Service interface for team-employee relationship operations. */
public interface TeamEmployeeService {

  /**
   * Add an employee to a team.
   *
   * @param teamCode the team code
   * @param request the employee addition request
   * @return the created TeamEmployeeDto
   */
  TeamEmployeeDto addEmployeeToTeam(String teamCode, TeamEmployeeAddRequest request);

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
   * Get all employees in a team.
   *
   * @param teamCode the team code
   * @return list of EmployeeDto
   */
  List<EmployeeDto> getEmployeesByTeamCode(String teamCode);

  /**
   * Get team lead by team code.
   *
   * @param teamCode the team code
   * @return EmployeeDto of team lead
   */
  EmployeeDto getTeamLeadByTeamCode(String teamCode);

  /**
   * Search employees in a team with filtering and pagination.
   *
   * @param teamCode the team code
   * @param request the search request
   * @return PageResponse of SearchTeamEmployeeDto
   */
  PageResponse<SearchTeamEmployeeDto> searchEmployeesInTeam(
      String teamCode, PaginationSearchRequest<SearchTeamEmployeeRequest> request);

  /**
   * Transfer an employee from one team to another.
   *
   * @param request the request containing transfer details
   * @return Map containing transfer confirmation details
   */
  Map<String, String> transferEmployee(TransferTeamEmployeeRequest request);
}
