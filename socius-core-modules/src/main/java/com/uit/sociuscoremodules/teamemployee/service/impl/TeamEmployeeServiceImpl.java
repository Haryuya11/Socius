package com.uit.sociuscoremodules.teamemployee.service.impl;

import com.uit.sociuscoremodules.employee.domain.Employee;
import com.uit.sociuscoremodules.employee.persistence.EmployeeMapper;
import com.uit.sociuscoremodules.shared.enums.DeleteFlagEnums;
import com.uit.sociuscoremodules.shared.service.impl.BaseServiceImpl;
import com.uit.sociuscoremodules.team.constants.TeamConstant;
import com.uit.sociuscoremodules.team.domain.Team;
import com.uit.sociuscoremodules.team.persistence.TeamMapper;
import com.uit.sociuscoremodules.teamemployee.constants.TeamEmployeeConstant;
import com.uit.sociuscoremodules.teamemployee.converter.TeamEmployeeConverter;
import com.uit.sociuscoremodules.teamemployee.domain.TeamEmployee;
import com.uit.sociuscoremodules.teamemployee.dto.TeamEmployeeBatchResult;
import com.uit.sociuscoremodules.teamemployee.dto.TeamEmployeeDto;
import com.uit.sociuscoremodules.teamemployee.enums.TeamRoleEnums;
import com.uit.sociuscoremodules.teamemployee.repository.TeamEmployeeRepository;
import com.uit.sociuscoremodules.teamemployee.request.TeamEmployeeAddRequest;
import com.uit.sociuscoremodules.teamemployee.request.TeamEmployeeBatchAddRequest;
import com.uit.sociuscoremodules.teamemployee.service.TeamEmployeeService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** Implementation of TeamEmployeeService for team-employee relationship operations. */
@Slf4j
@Service
@RequiredArgsConstructor
public class TeamEmployeeServiceImpl extends BaseServiceImpl implements TeamEmployeeService {

  private final TeamEmployeeRepository teamEmployeeRepository;
  private final TeamMapper teamMapper;
  private final EmployeeMapper employeeMapper;
  private final TeamEmployeeConverter teamEmployeeConverter;

  /**
   * Add an employee to a team with specified role and leadership status.
   *
   * <p>Validates team and employee existence, checks for duplicates, and ensures only one team lead
   * per team.
   *
   * @param teamCode the team code
   * @param employeeId the employee client ID
   * @param roleCode the role code (TEAM_LEAD or TEAM_MEM)
   * @param isLeader whether the employee is team leader
   * @return the created TeamEmployee entity
   * @throws NotFoundException if team or employee not found
   * @throws BadRequestException if employee already in team or team already has a lead
   */
  @Override
  public TeamEmployee addEmployeeToTeam(
      String teamCode, String employeeId, String roleCode, Boolean isLeader) {
    log.info(
        "Adding employee {} to team {} with role {} and leadership status {}",
        employeeId,
        teamCode,
        roleCode,
        isLeader);

    Team team = teamMapper.findByTeamCode(teamCode);
    if (team == null) {
      log.error("Team not found with code: {}", teamCode);
      throw notFound(TeamConstant.TEAM_NOT_FOUND);
    }

    Employee employee = employeeMapper.findByClientId(employeeId);
    if (employee == null) {
      log.error("Employee not found with client ID: {}", employeeId);
      throw notFound(TeamEmployeeConstant.EMPLOYEE_NOT_FOUND);
    }

    // Check if employee is already active in team
    if (Boolean.TRUE.equals(
        teamEmployeeRepository.existsByTeamCodeAndEmployeeId(teamCode, employeeId))) {
      log.error("Employee {} is already in team {}", employeeId, teamCode);
      throw badRequest(TeamEmployeeConstant.EMPLOYEE_ALREADY_IN_TEAM);
    }

    // Check if there's a soft-deleted record to reactivate
    TeamEmployee softDeletedRecord =
        teamEmployeeRepository.findSoftDeletedByTeamCodeAndEmployeeId(teamCode, employeeId);

    if (softDeletedRecord != null) {
      log.info(
          "Found soft-deleted record for employee {} in team {}, reactivating...",
          employeeId,
          teamCode);

      // Check team lead constraint before reactivating
      if (Boolean.TRUE.equals(isLeader)
          && Boolean.TRUE.equals(teamEmployeeRepository.existsTeamLeadByTeamCode(teamCode))) {
        log.error("Team {} already has a team lead", teamCode);
        throw badRequest(TeamEmployeeConstant.TEAM_LEAD_ALREADY_EXISTS);
      }

      // Reactivate the soft-deleted record with new values
      teamEmployeeRepository.reactivate(teamCode, employeeId, roleCode, isLeader);
      log.info("Successfully reactivated employee {} in team {}", employeeId, teamCode);

      // Return the reactivated record
      return teamEmployeeRepository.findByTeamCodeAndEmployeeId(teamCode, employeeId);
    }

    // No soft-deleted record found, check team lead constraint for new record
    if (Boolean.TRUE.equals(isLeader)
        && Boolean.TRUE.equals(teamEmployeeRepository.existsTeamLeadByTeamCode(teamCode))) {
      log.error("Team {} already has a team lead", teamCode);
      throw badRequest(TeamEmployeeConstant.TEAM_LEAD_ALREADY_EXISTS);
    }

    // Create new team-employee record
    TeamEmployee teamEmployee = new TeamEmployee();
    teamEmployee.setTeamCode(teamCode);
    teamEmployee.setEmployeeId(employeeId);
    teamEmployee.setRoleCode(roleCode);
    teamEmployee.setIsLeader(isLeader);
    teamEmployee.setCreatedAt(LocalDateTime.now());
    teamEmployee.setUpdatedAt(LocalDateTime.now());
    teamEmployee.setDeleteFlag(DeleteFlagEnums.NOT_DELETED.getValue().shortValue());

    teamEmployeeRepository.insert(teamEmployee);
    log.info("Successfully added employee {} to team {}", employeeId, teamCode);

    return teamEmployee;
  }

  /**
   * Add an employee to a team using request object.
   *
   * <p>Automatically determines role code based on isLeader flag. Returns enriched DTO with
   * employee details.
   *
   * @param teamCode the team code
   * @param request the request containing employee client ID, optional role code, and isLeader flag
   * @return TeamEmployeeDto with employee information
   * @throws NotFoundException if team or employee not found
   * @throws BadRequestException if employee already in team or team already has a lead
   */
  @Override
  public TeamEmployeeDto addEmployeeToTeam(String teamCode, TeamEmployeeAddRequest request) {
    log.info("Adding employee to team {} with request: {}", teamCode, request);

    String roleCode =
        Boolean.TRUE.equals(request.getIsLeader())
            ? TeamRoleEnums.TEAM_LEAD.getRoleCode()
            : TeamRoleEnums.EMPLOYEE.getRoleCode();

    if (request.getRoleCode() != null && !request.getRoleCode().isEmpty()) {
      roleCode = request.getRoleCode();
    }

    TeamEmployee teamEmployee =
        addEmployeeToTeam(teamCode, request.getClientId(), roleCode, request.getIsLeader());

    Employee employee = employeeMapper.findByClientId(request.getClientId());
    TeamEmployeeDto dto = teamEmployeeConverter.entityToDto(teamEmployee);
    dto.setUserId(employee.getUserId());
    dto.setFirstName(employee.getFirstName());
    dto.setLastName(employee.getLastName());
    dto.setImageUrl(employee.getImageUrl());

    return dto;
  }

  /**
   * Add multiple employees to a team in batch.
   *
   * <p>Processes each employee individually, collecting successful additions and failures. Does not
   * stop on first error, continues processing remaining employees.
   *
   * @param teamCode the team code
   * @param request the batch request containing list of employees to add
   * @return TeamEmployeeBatchResult with lists of successful additions and failures with error
   *     details
   */
  @Override
  public TeamEmployeeBatchResult addEmployeesToTeam(
      String teamCode, TeamEmployeeBatchAddRequest request) {
    log.info("Adding batch of {} employees to team {}", request.getEmployees().size(), teamCode);

    List<TeamEmployeeDto> successful = new ArrayList<>();
    List<TeamEmployeeBatchResult.BatchError> failed = new ArrayList<>();

    for (TeamEmployeeAddRequest employeeRequest : request.getEmployees()) {
      try {
        TeamEmployeeDto dto = addEmployeeToTeam(teamCode, employeeRequest);
        successful.add(dto);
        log.info(
            "Successfully added employee {} to team {}", employeeRequest.getClientId(), teamCode);
      } catch (Exception e) {
        log.error(
            "Failed to add employee {} to team {}: {}",
            employeeRequest.getClientId(),
            teamCode,
            e.getMessage());

        TeamEmployeeBatchResult.BatchError error =
            TeamEmployeeBatchResult.BatchError.builder()
                .clientId(employeeRequest.getClientId())
                .errorCode("ADD_FAILED")
                .errorMessage(e.getMessage())
                .build();
        failed.add(error);
      }
    }

    log.info("Batch add completed: {} successful, {} failed", successful.size(), failed.size());

    return TeamEmployeeBatchResult.builder().successful(successful).failed(failed).build();
  }

  /**
   * Remove an employee from a team (soft delete).
   *
   * @param teamCode the team code
   * @param employeeId the employee client ID to remove
   * @throws NotFoundException if team not found or employee not in team
   */
  @Override
  public void removeEmployeeFromTeam(String teamCode, String employeeId) {
    log.info("Removing employee {} from team {}", employeeId, teamCode);

    Team team = teamMapper.findByTeamCode(teamCode);
    if (team == null) {
      log.error("Team not found with code: {}", teamCode);
      throw notFound(TeamConstant.TEAM_NOT_FOUND);
    }

    TeamEmployee teamEmployee =
        teamEmployeeRepository.findByTeamCodeAndEmployeeId(teamCode, employeeId);
    if (teamEmployee == null) {
      log.error("Employee {} is not in team {}", employeeId, teamCode);
      throw badRequest(TeamEmployeeConstant.EMPLOYEE_NOT_IN_TEAM);
    }

    teamEmployeeRepository.softDelete(teamCode, employeeId);
    log.info("Successfully removed employee {} from team {}", employeeId, teamCode);
  }

  /**
   * Change the team lead to a different employee.
   *
   * <p>Removes leadership from current lead (if exists) and assigns it to the new lead. New lead
   * must already be a member of the team.
   *
   * @param teamCode the team code
   * @param newLeadClientId the client ID of the new team lead
   * @return TeamEmployeeDto of the new team lead with updated information
   * @throws NotFoundException if team or employee not found
   * @throws BadRequestException if new lead is not a member of the team
   */
  @Override
  public TeamEmployeeDto changeTeamLead(String teamCode, String newLeadClientId) {
    log.info("Changing team lead for team {} to employee {}", teamCode, newLeadClientId);

    Team team = teamMapper.findByTeamCode(teamCode);
    if (team == null) {
      log.error("Team not found with code: {}", teamCode);
      throw notFound(TeamConstant.TEAM_NOT_FOUND);
    }

    Employee newLeader = employeeMapper.findByClientId(newLeadClientId);
    if (newLeader == null) {
      log.error("Employee not found with client ID: {}", newLeadClientId);
      throw notFound(TeamEmployeeConstant.EMPLOYEE_NOT_FOUND);
    }

    TeamEmployee newLeaderTeamEmployee =
        teamEmployeeRepository.findByTeamCodeAndEmployeeId(teamCode, newLeadClientId);
    if (newLeaderTeamEmployee == null) {
      log.error("Employee {} is not in team {}", newLeadClientId, teamCode);
      throw badRequest(TeamEmployeeConstant.EMPLOYEE_NOT_IN_TEAM);
    }

    Employee currentLead = teamEmployeeRepository.findTeamLeadByTeamCode(teamCode);

    if (currentLead != null && !currentLead.getClientId().equals(newLeadClientId)) {
      teamEmployeeRepository.updateLeadershipStatus(teamCode, currentLead.getClientId(), false);
      log.info("Removed leadership from current lead: {}", currentLead.getClientId());
    }

    teamEmployeeRepository.updateLeadershipStatus(teamCode, newLeadClientId, true);
    log.info("Successfully changed team lead to: {}", newLeadClientId);

    TeamEmployee updatedTeamEmployee =
        teamEmployeeRepository.findByTeamCodeAndEmployeeId(teamCode, newLeadClientId);
    TeamEmployeeDto dto = teamEmployeeConverter.entityToDto(updatedTeamEmployee);
    dto.setUserId(newLeader.getUserId());
    dto.setFirstName(newLeader.getFirstName());
    dto.setLastName(newLeader.getLastName());
    dto.setImageUrl(newLeader.getImageUrl());

    return dto;
  }

  /**
   * Get all employees in a team.
   *
   * @param teamCode the team code
   * @return list of all Employee entities in the team
   * @throws NotFoundException if team not found
   */
  @Override
  public List<Employee> getEmployeesByTeamCode(String teamCode) {
    log.info("Getting all employees in team {}", teamCode);

    Team team = teamMapper.findByTeamCode(teamCode);
    if (team == null) {
      log.error("Team not found with code: {}", teamCode);
      throw notFound(TeamConstant.TEAM_NOT_FOUND);
    }

    return teamEmployeeRepository.findEmployeesByTeamCode(teamCode);
  }

  /**
   * Get the team lead of a team.
   *
   * @param teamCode the team code
   * @return the Employee entity of the team lead, or null if no team lead assigned
   * @throws NotFoundException if team not found
   */
  @Override
  public Employee getTeamLeadByTeamCode(String teamCode) {
    log.info("Getting team lead for team {}", teamCode);

    Team team = teamMapper.findByTeamCode(teamCode);
    if (team == null) {
      log.error("Team not found with code: {}", teamCode);
      throw notFound(TeamConstant.TEAM_NOT_FOUND);
    }

    return teamEmployeeRepository.findTeamLeadByTeamCode(teamCode);
  }

  /**
   * Search employees in a team with filters, sorting, and pagination.
   *
   * @param teamCode the team code
   * @param request the search request with filters
   * @return map containing list of employees and pagination info
   * @throws NotFoundException if team not found
   */
  @Override
  public java.util.Map<String, Object> searchEmployeesInTeam(
      String teamCode,
      com.uit.sociuscoremodules.shared.request.PaginationSearchRequest<
              com.uit.sociuscoremodules.teamemployee.request.TeamEmployeeSearchCondition>
          request) {
    log.info("Searching employees in team {} with request: {}", teamCode, request);

    Team team = teamMapper.findByTeamCode(teamCode);
    if (team == null) {
      log.error("Team not found with code: {}", teamCode);
      throw notFound(TeamConstant.TEAM_NOT_FOUND);
    }

    // Convert PaginationSearchRequest to TeamEmployeeSearchRequest
    com.uit.sociuscoremodules.teamemployee.request.TeamEmployeeSearchRequest searchRequest =
        new com.uit.sociuscoremodules.teamemployee.request.TeamEmployeeSearchRequest();
    searchRequest.setTeamCode(teamCode);

    // Set search conditions
    if (request.getCondition() != null) {
      searchRequest.setUserId(request.getCondition().getUserId());
      searchRequest.setFirstName(request.getCondition().getFirstName());
      searchRequest.setLastName(request.getCondition().getLastName());
      searchRequest.setRoleCode(request.getCondition().getRoleCode());
    }

    // Set pagination
    if (request.getPageRequest() != null) {
      searchRequest.setPage(request.getPageRequest().getPageNumber());
      searchRequest.setSize(request.getPageRequest().getPageSize());
    }

    // Set sorting
    if (request.getSortRequests() != null && !request.getSortRequests().isEmpty()) {
      searchRequest.setSortBy(request.getSortRequests().get(0).getSortBy());
      searchRequest.setSortOrder(request.getSortRequests().get(0).getSortDirection());
    }

    return teamEmployeeRepository.searchEmployeesInTeam(searchRequest);
  }
}
