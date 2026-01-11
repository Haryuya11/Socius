package com.uit.sociusmvcapp.workforce.internal.repository;

import com.uit.sociusmvcapp.workforce.dto.TeamEmployeeDto;
import com.uit.sociusmvcapp.workforce.dto.request.AddEmployeeToTeamRequest;
import com.uit.sociusmvcapp.workforce.internal.converter.TeamEmployeeConverter;
import com.uit.sociusmvcapp.workforce.internal.persistence.TeamEmployeeMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/** Repository for TeamEmployee entity. */
@Repository
@RequiredArgsConstructor
public class TeamEmployeeRepository {
  private final TeamEmployeeMapper mapper;
  private final TeamEmployeeConverter converter;

  /**
   * Find TeamEmployee by team code and employee ID.
   *
   * @param teamCode the team code
   * @param employeeId the employee ID (client_id)
   * @return the corresponding TeamEmployeeDto
   */
  public TeamEmployeeDto findByTeamCodeAndEmployeeId(String teamCode, String employeeId) {
    return converter.entityToDto(mapper.findByTeamCodeAndEmployeeId(teamCode, employeeId));
  }

  /**
   * Find all employees in a team.
   *
   * @param teamCode the team code
   * @return list of TeamEmployeeDto
   */
  public List<TeamEmployeeDto> findEmployeesByTeamCode(String teamCode) {
    return converter.entitiesToDtos(mapper.findEmployeesByTeamCode(teamCode));
  }

  /**
   * Find team lead of a team.
   *
   * @param teamCode the team code
   * @return the Employee entity
   */
  public TeamEmployeeDto findTeamLeadByTeamCode(String teamCode) {
    return converter.entityToDto(mapper.findTeamLeadByTeamCode(teamCode));
  }

  /**
   * Batch insert team-employee relationships.
   *
   * @param teamCode the team code
   * @param requests the list of add employee requests
   */
  public void batchInsert(String teamCode, List<AddEmployeeToTeamRequest> requests) {
    mapper.batchInsert(requests.stream().map(req -> converter.toEntity(req, teamCode)).toList());
  }

  /**
   * Batch reactivate soft-deleted team-employee relationships.
   *
   * @param teamCode the team code
   * @param requests the list of add employee requests
   */
  public void batchReactivate(String teamCode, List<AddEmployeeToTeamRequest> requests) {
    mapper.batchReactivate(
        requests.stream().map(req -> converter.toEntity(req, teamCode)).toList());
  }

  /**
   * Find soft-deleted team-employee relationships by team code and employee IDs.
   *
   * @param teamCode the team code
   * @param employeeIds the list of employee IDs
   * @return list of TeamEmployeeDto
   */
  public List<TeamEmployeeDto> findSoftDeletedByTeamCodeAndEmployeeIds(
      String teamCode, List<String> employeeIds) {
    return converter.entitiesToDtos(
        mapper.findSoftDeletedByTeamCodeAndEmployeeIds(teamCode, employeeIds));
  }

  /**
   * Update leadership status for an employee in a team.
   *
   * @param teamCode the team code
   * @param employeeId the employee ID
   * @param isLeader the leadership status
   */
  public void updateLeadershipStatus(String teamCode, String employeeId, Boolean isLeader) {
    mapper.updateLeadershipStatus(teamCode, employeeId, isLeader);
  }

  /**
   * Batch remove employees from a team.
   *
   * @param teamCode the team code
   * @param employeeIds the list of employee IDs to remove
   */
  public void batchRemoveEmployees(String teamCode, List<String> employeeIds) {
    mapper.batchSoftDelete(teamCode, employeeIds);
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
    return converter.entityToDto(
        mapper.findSoftDeletedByTeamCodeAndEmployeeId(teamCode, employeeId));
  }

  /**
   * Reactivate soft-deleted team-employee relationship.
   *
   * @param teamCode the team code
   * @param request the request containing details
   */
  public void reactivate(String teamCode, AddEmployeeToTeamRequest request) {
    mapper.reactivate(converter.toEntity(request, teamCode));
  }

  /**
   * Check if employee is already in team.
   *
   * @param teamCode the team code
   * @param employeeId the employee ID (user_id)
   * @return true if exists, false otherwise
   */
  public Boolean existsByTeamCodeAndEmployeeId(String teamCode, String employeeId) {
    return mapper.existsByTeamCodeAndEmployeeId(teamCode, employeeId);
  }

  /**
   * Check if team has a team lead.
   *
   * @param teamCode the team code
   * @return true if exists, false otherwise
   */
  public Boolean existsTeamLeadByTeamCode(String teamCode) {
    return mapper.existsTeamLeadByTeamCode(teamCode);
  }

  /**
   * Update role code for an employee in a team.
   *
   * @param teamCode the team code
   * @param employeeId the employee ID
   * @param roleCode the role code
   */
  public void updateRoleCode(String teamCode, String employeeId, String roleCode) {
    mapper.updateRoleCode(teamCode, employeeId, roleCode);
  }

  /**
   * Check if a team has any employees.
   *
   * @param teamCode the team code
   */
  public boolean hasEmployees(String teamCode) {
    return mapper.hasEmployees(teamCode);
  }

  /**
   * Delete all team-employee relationships for a given employee ID.
   *
   * @param employeeId the employee ID
   */
  public void deleteByEmployeeId(String employeeId) {
    mapper.deleteByEmployeeId(employeeId);
  }

  /**
   * Find all team-employee relationships for a given employee ID.
   *
   * @param employeeId the employee ID
   * @return list of TeamEmployeeDto
   */
  public List<TeamEmployeeDto> findByEmployeeId(String employeeId) {
    return converter.entitiesToDtos(mapper.findByEmployeeId(employeeId));
  }

  /**
   * Find all team-employee relationships for a list of employee IDs.
   *
   * @param employeeIds the list of employee IDs
   * @return list of TeamEmployeeDto
   */
  public List<TeamEmployeeDto> findByEmployeeIdIn(List<String> employeeIds) {
    return converter.entitiesToDtos(mapper.findByEmployeeIdIn(employeeIds));
  }
}
