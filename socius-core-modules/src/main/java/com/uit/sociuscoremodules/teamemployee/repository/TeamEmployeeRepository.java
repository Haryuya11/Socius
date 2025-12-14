package com.uit.sociuscoremodules.teamemployee.repository;

import com.uit.sociuscoremodules.shared.request.SortRequest;
import com.uit.sociuscoremodules.teamemployee.converter.TeamEmployeeConverter;
import com.uit.sociuscoremodules.teamemployee.dto.SearchTeamEmployeeDto;
import com.uit.sociuscoremodules.teamemployee.dto.TeamEmployeeDto;
import com.uit.sociuscoremodules.teamemployee.persistence.TeamEmployeeMapper;
import com.uit.sociuscoremodules.teamemployee.request.SearchTeamEmployeeRequest;
import com.uit.sociuscoremodules.teamemployee.request.TeamEmployeeAddRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/** Repository for TeamEmployee entity. */
@Repository
@RequiredArgsConstructor
public class TeamEmployeeRepository {
  private final TeamEmployeeMapper teamEmployeeMapper;
  private final TeamEmployeeConverter teamEmployeeConverter;

  /**
   * Find TeamEmployee by team code and employee ID.
   *
   * @param teamCode the team code
   * @param employeeId the employee ID (client_id)
   * @return the corresponding TeamEmployeeDto
   */
  public TeamEmployeeDto findByTeamCodeAndEmployeeId(String teamCode, String employeeId) {
    return teamEmployeeConverter.entityToDto(
        teamEmployeeMapper.findByTeamCodeAndEmployeeId(teamCode, employeeId));
  }

  /**
   * Find all employees in a team.
   *
   * @param teamCode the team code
   * @return list of TeamEmployeeDto
   */
  public List<TeamEmployeeDto> findEmployeesByTeamCode(String teamCode) {
    return teamEmployeeMapper.findEmployeesByTeamCode(teamCode);
  }

  /**
   * Find team lead of a team.
   *
   * @param teamCode the team code
   * @return the Employee entity
   */
  public TeamEmployeeDto findTeamLeadByTeamCode(String teamCode) {
    return teamEmployeeConverter.entityToDto(teamEmployeeMapper.findTeamLeadByTeamCode(teamCode));
  }

  /**
   * Add an employee to a team.
   *
   * @param request the TeamEmployeeAddRequest
   * @param teamCode the team code
   * @param employeeId the employee ID
   */
  public void addEmployeeToTeam(
      TeamEmployeeAddRequest request, String teamCode, String employeeId) {
    teamEmployeeMapper.insert(
        teamEmployeeConverter.createTeamEmployeeRequestToEntity(request, teamCode, employeeId));
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
   * Remove an employee from a team.
   *
   * @param teamCode the team code
   * @param employeeId the employee ID
   */
  public void removeEmployeeFromTeam(String teamCode, String employeeId) {
    teamEmployeeMapper.softDelete(teamCode, employeeId);
  }

  /**
   * Find soft-deleted TeamEmployee by team code and employee ID.
   *
   * @param teamCode the team code
   * @param employeeId the employee ID
   * @return the corresponding TeamEmployeeDto
   */
  public TeamEmployeeDto findSoftDeletedByTeamCodeAndEmployeeId(
      String teamCode, String employeeId) {
    return teamEmployeeConverter.entityToDto(
        teamEmployeeMapper.findSoftDeletedByTeamCodeAndEmployeeId(teamCode, employeeId));
  }

  /**
   * Reactivate soft-deleted team-employee relationship.
   *
   * @param teamCode the team code
   * @param request the request containing details
   */
  public void reactivate(String teamCode, TeamEmployeeAddRequest request) {
    teamEmployeeMapper.reactivate(teamEmployeeConverter.toEntity(teamCode, request));
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
   * Count employees in a team based on given criteria.
   *
   * @param criteria the search criteria
   * @return the count of employees matching the criteria
   */
  public int count(SearchTeamEmployeeRequest criteria) {
    return teamEmployeeMapper.count(criteria);
  }

  /**
   * Search with pagination.
   *
   * @param criteria the search criteria
   * @param sortRequests the sorting requests
   * @param limit limit
   * @param offset offset
   * @return List of SearchTeamEmployeeDto
   */
  public List<SearchTeamEmployeeDto> search(
      SearchTeamEmployeeRequest criteria, List<SortRequest> sortRequests, int limit, int offset) {
    return teamEmployeeConverter.dtosToSearchDtos(
        teamEmployeeMapper.search(criteria, sortRequests, limit, offset));
  }

  /**
   * Update role code for an employee in a team.
   *
   * @param teamCode the team code
   * @param employeeId the employee ID
   * @param roleCode the role code
   */
  public void updateRoleCode(String teamCode, String employeeId, String roleCode) {
    teamEmployeeMapper.updateRoleCode(teamCode, employeeId, roleCode);
  }
}
