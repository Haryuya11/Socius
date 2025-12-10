package com.uit.sociuscoremodules.teamemployee.service;

import com.uit.sociuscoremodules.employee.domain.Employee;
import com.uit.sociuscoremodules.teamemployee.domain.TeamEmployee;
import com.uit.sociuscoremodules.teamemployee.dto.TeamEmployeeBatchResult;
import com.uit.sociuscoremodules.teamemployee.dto.TeamEmployeeDto;
import com.uit.sociuscoremodules.teamemployee.request.TeamEmployeeAddRequest;
import com.uit.sociuscoremodules.teamemployee.request.TeamEmployeeBatchAddRequest;
import java.util.List;

/** Service interface for TeamEmployee-related operations. */
public interface TeamEmployeeService {

  /**
   * Add an employee to a team.
   *
   * @param teamCode the team code
   * @param employeeId the employee ID (client_id)
   * @param roleCode the role code (TEAM_LEAD or TEAM_MEM)
   * @param isLeader whether the employee is team leader
   * @return the created TeamEmployee
   */
  TeamEmployee addEmployeeToTeam(
      String teamCode, String employeeId, String roleCode, Boolean isLeader);

  /**
   * Add an employee to a team using teamCode and request.
   *
   * @param teamCode the team code
   * @param request the request containing employee addition details
   * @return the created TeamEmployeeDto
   */
  TeamEmployeeDto addEmployeeToTeam(String teamCode, TeamEmployeeAddRequest request);

  /**
   * Add multiple employees to a team.
   *
   * @param teamCode the team code
   * @param request the request containing list of employees to add
   * @return the batch result with successful and failed operations
   */
  TeamEmployeeBatchResult addEmployeesToTeam(String teamCode, TeamEmployeeBatchAddRequest request);

  /**
   * Remove an employee from a team.
   *
   * @param teamCode the team code
   * @param employeeId the employee ID (client_id)
   */
  void removeEmployeeFromTeam(String teamCode, String employeeId);

  /**
   * Change team lead.
   *
   * @param teamCode the team code
   * @param newLeadClientId the client ID of the new team lead
   * @return the updated team lead TeamEmployeeDto
   */
  TeamEmployeeDto changeTeamLead(String teamCode, String newLeadClientId);

  /**
   * Get all employees in a team.
   *
   * @param teamCode the team code
   * @return list of Employee entities
   */
  List<Employee> getEmployeesByTeamCode(String teamCode);

  /**
   * Get team lead of a team.
   *
   * @param teamCode the team code
   * @return the Employee entity
   */
  Employee getTeamLeadByTeamCode(String teamCode);

  /**
   * Search employees in a team with filters, sorting, and pagination.
   *
   * @param teamCode the team code
   * @param request the search request with filters
   * @return map containing list of employees and pagination info
   */
  java.util.Map<String, Object> searchEmployeesInTeam(
      String teamCode,
      com.uit.sociuscoremodules.shared.request.PaginationSearchRequest<
              com.uit.sociuscoremodules.teamemployee.request.TeamEmployeeSearchCondition>
          request);
}
