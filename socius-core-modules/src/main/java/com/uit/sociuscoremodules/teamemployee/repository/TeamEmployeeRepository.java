package com.uit.sociuscoremodules.teamemployee.repository;

import com.uit.sociuscoremodules.employee.domain.Employee;
import com.uit.sociuscoremodules.teamemployee.domain.TeamEmployee;
import com.uit.sociuscoremodules.teamemployee.persistence.TeamEmployeeMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/** Repository for TeamEmployee entity. */
@Repository
@RequiredArgsConstructor
public class TeamEmployeeRepository {
  private final TeamEmployeeMapper teamEmployeeMapper;

  /**
   * Find team-employee relationship by team code and employee ID.
   *
   * @param teamCode the team code
   * @param employeeId the employee ID (user_id)
   * @return the TeamEmployee entity
   */
  public TeamEmployee findByTeamCodeAndEmployeeId(String teamCode, String employeeId) {
    return teamEmployeeMapper.findByTeamCodeAndEmployeeId(teamCode, employeeId);
  }

  /**
   * Find all employees in a team.
   *
   * @param teamCode the team code
   * @return list of Employee entities
   */
  public List<Employee> findEmployeesByTeamCode(String teamCode) {
    return teamEmployeeMapper.findEmployeesByTeamCode(teamCode);
  }

  /**
   * Find team lead of a team.
   *
   * @param teamCode the team code
   * @return the Employee entity
   */
  public Employee findTeamLeadByTeamCode(String teamCode) {
    return teamEmployeeMapper.findTeamLeadByTeamCode(teamCode);
  }

  /**
   * Add an employee to a team.
   *
   * @param teamEmployee the TeamEmployee entity
   */
  public void insert(TeamEmployee teamEmployee) {
    teamEmployeeMapper.insert(teamEmployee);
  }

  /**
   * Update leadership status for an employee in a team.
   *
   * @param teamCode the team code
   * @param employeeId the employee ID (user_id)
   * @param isLeader the leadership status
   */
  public void updateLeadershipStatus(String teamCode, String employeeId, Boolean isLeader) {
    teamEmployeeMapper.updateLeadershipStatus(teamCode, employeeId, isLeader);
  }

  /**
   * Remove an employee from a team (soft delete).
   *
   * @param teamCode the team code
   * @param employeeId the employee ID (user_id)
   */
  public void softDelete(String teamCode, String employeeId) {
    teamEmployeeMapper.softDelete(teamCode, employeeId);
  }

  /**
   * Find soft-deleted team-employee relationship.
   *
   * @param teamCode the team code
   * @param employeeId the employee ID (user_id)
   * @return the soft-deleted TeamEmployee entity or null
   */
  public TeamEmployee findSoftDeletedByTeamCodeAndEmployeeId(String teamCode, String employeeId) {
    return teamEmployeeMapper.findSoftDeletedByTeamCodeAndEmployeeId(teamCode, employeeId);
  }

  /**
   * Reactivate soft-deleted team-employee relationship.
   *
   * @param teamCode the team code
   * @param employeeId the employee ID
   * @param roleCode the role code
   * @param isLeader the leadership status
   */
  public void reactivate(String teamCode, String employeeId, String roleCode, Boolean isLeader) {
    teamEmployeeMapper.reactivate(teamCode, employeeId, roleCode, isLeader);
  }

  /**
   * Check if employee is already in team.
   *
   * @param teamCode the team code
   * @param employeeId the employee ID (user_id)
   * @return true if exists, false otherwise
   */
  public Boolean existsByTeamCodeAndEmployeeId(String teamCode, String employeeId) {
    return teamEmployeeMapper.existsByTeamCodeAndEmployeeId(teamCode, employeeId);
  }

  /**
   * Check if team has a team lead.
   *
   * @param teamCode the team code
   * @return true if exists, false otherwise
   */
  public Boolean existsTeamLeadByTeamCode(String teamCode) {
    return teamEmployeeMapper.existsTeamLeadByTeamCode(teamCode);
  }

  /**
   * Count team leaders in a team.
   *
   * @param teamCode the team code
   * @return count of team leaders
   */
  public Integer countTeamLeadersByTeamCode(String teamCode) {
    return teamEmployeeMapper.countTeamLeadersByTeamCode(teamCode);
  }

  /**
   * Search employees in a team with filters, sorting, and pagination.
   *
   * @param request the search request with filters
   * @return map containing list of TeamEmployeeDto and total count
   */
  public java.util.Map<String, Object> searchEmployeesInTeam(
      com.uit.sociuscoremodules.teamemployee.request.TeamEmployeeSearchRequest request) {
    List<com.uit.sociuscoremodules.teamemployee.dto.TeamEmployeeDto> employees =
        teamEmployeeMapper.searchEmployeesInTeam(request);
    Integer total = teamEmployeeMapper.countEmployeesInTeam(request);

    java.util.Map<String, Object> result = new java.util.HashMap<>();
    result.put("employees", employees);
    result.put("total", total);
    return result;
  }
}
