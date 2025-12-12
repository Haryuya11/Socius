package com.uit.sociuscoremodules.teamemployee.service.impl;

import com.uit.sociuscoremodules.employee.domain.Employee;
import com.uit.sociuscoremodules.employee.dto.EmployeeDto;
import com.uit.sociuscoremodules.employee.persistence.EmployeeMapper;
import com.uit.sociuscoremodules.shared.constants.CommonConstant;
import com.uit.sociuscoremodules.shared.constants.MessageConstant;
import com.uit.sociuscoremodules.shared.enums.DeleteFlagEnums;
import com.uit.sociuscoremodules.shared.request.PaginationSearchRequest;
import com.uit.sociuscoremodules.shared.response.PageResponse;
import com.uit.sociuscoremodules.shared.service.I18nService;
import com.uit.sociuscoremodules.shared.service.impl.BaseServiceImpl;
import com.uit.sociuscoremodules.team.domain.Team;
import com.uit.sociuscoremodules.team.persistence.TeamMapper;
import com.uit.sociuscoremodules.teamemployee.converter.TeamEmployeeConverter;
import com.uit.sociuscoremodules.teamemployee.domain.TeamEmployee;
import com.uit.sociuscoremodules.teamemployee.dto.SearchTeamEmployeeDto;
import com.uit.sociuscoremodules.teamemployee.dto.TeamEmployeeBatchResult;
import com.uit.sociuscoremodules.teamemployee.dto.TeamEmployeeDto;
import com.uit.sociuscoremodules.teamemployee.enums.TeamRoleEnums;
import com.uit.sociuscoremodules.teamemployee.repository.TeamEmployeeRepository;
import com.uit.sociuscoremodules.teamemployee.request.SearchTeamEmployeeRequest;
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
  private final I18nService i18nService;

  // ========================= TEAM EMPLOYEE SERVICE MAIN METHODS =========================

  /** Add an employee to a team with specified role and leadership status. */
  @Override
  public TeamEmployee addEmployeeToTeam(
      String teamCode, String employeeId, String roleCode, Boolean isLeader) {

    // Note: We don't return 'Team' here because this method signature is fixed in Interface
    // to return TeamEmployee entity only.
    validateTeamExists(teamCode);
    validateEmployeeExists(employeeId);
    validateEmployeeNotActiveInTeam(teamCode, employeeId);

    // Check if there's a soft-deleted record to reactivate
    TeamEmployee softDeletedRecord =
        teamEmployeeRepository.findSoftDeletedByTeamCodeAndEmployeeId(teamCode, employeeId);

    if (softDeletedRecord != null) {
      return reactivateEmployeeInTeam(teamCode, employeeId, roleCode, isLeader);
    }

    // Create new record
    return createNewTeamEmployee(teamCode, employeeId, roleCode, isLeader);
  }

  /** Add an employee to a team using request object. */
  @Override
  public TeamEmployeeDto addEmployeeToTeam(String teamCode, TeamEmployeeAddRequest request) {
    Team team = validateTeamExists(teamCode);
    return processAddEmployeeToTeam(team, request);
  }

  /** Add multiple employees to a team in batch. */
  @Override
  public TeamEmployeeBatchResult addEmployeesToTeam(
      String teamCode, TeamEmployeeBatchAddRequest request) {

    // Validate team once for the whole batch
    Team team = validateTeamExists(teamCode);

    List<TeamEmployeeDto> successful = new ArrayList<>();
    List<TeamEmployeeBatchResult.BatchError> failed = new ArrayList<>();

    for (TeamEmployeeAddRequest employeeRequest : request.getEmployees()) {
      try {
        TeamEmployeeDto dto = processAddEmployeeToTeam(team, employeeRequest);
        successful.add(dto);
      } catch (Exception e) {
        String errorMessage = i18nService.getMessage(e.getMessage());
        // If message is same as code (translation missing), keep original or handle fallback if
        // needed
        failed.add(buildBatchError(employeeRequest.getEmployeeId(), errorMessage));
      }
    }

    return TeamEmployeeBatchResult.builder().successful(successful).failed(failed).build();
  }

  /** Remove an employee from a team (soft delete). */
  @Override
  public void removeEmployeeFromTeam(String teamCode, String employeeId) {
    validateTeamExists(teamCode);

    TeamEmployee teamEmployee =
        teamEmployeeRepository.findByTeamCodeAndEmployeeId(teamCode, employeeId);
    if (teamEmployee == null) {
      throw badRequest(MessageConstant.E_TEAM_EMP_002);
    }

    teamEmployeeRepository.softDelete(teamCode, employeeId);
  }

  /** Change the team lead to a different employee. */
  @Override
  public TeamEmployeeDto changeTeamLead(String teamCode, String newLeadClientId) {
    final Team team = validateTeamExists(teamCode);
    final Employee newLeader = validateEmployeeExists(newLeadClientId);

    TeamEmployee newLeaderTeamEmployee =
        teamEmployeeRepository.findByTeamCodeAndEmployeeId(teamCode, newLeadClientId);
    if (newLeaderTeamEmployee == null) {
      throw badRequest(MessageConstant.E_TEAM_EMP_002);
    }

    // Remove leadership from old leader
    EmployeeDto currentLead = teamEmployeeRepository.findTeamLeadByTeamCode(teamCode);
    if (currentLead != null && !currentLead.getClientId().equals(newLeadClientId)) {
      teamEmployeeRepository.updateLeadershipStatus(teamCode, currentLead.getClientId(), false);
    }

    // Assign leadership to new leader
    teamEmployeeRepository.updateLeadershipStatus(teamCode, newLeadClientId, true);

    TeamEmployee updatedTeamEmployee =
        teamEmployeeRepository.findByTeamCodeAndEmployeeId(teamCode, newLeadClientId);

    // Re-use the existing newLeader employee object and team object
    return buildTeamEmployeeDto(updatedTeamEmployee, newLeader, team);
  }

  /** Get all employees in a team. */
  @Override
  public List<Employee> getEmployeesByTeamCode(String teamCode) {
    validateTeamExists(teamCode);
    return teamEmployeeRepository.findEmployeesByTeamCode(teamCode);
  }

  /** Get the team lead of a team. */
  @Override
  public EmployeeDto getTeamLeadByTeamCode(String teamCode) {
    validateTeamExists(teamCode);
    return teamEmployeeRepository.findTeamLeadByTeamCode(teamCode);
  }

  /** Search employees in a team with filters. */
  @Override
  public PageResponse<SearchTeamEmployeeDto> searchEmployeesInTeam(
      String teamCode, PaginationSearchRequest<SearchTeamEmployeeRequest> request) {
    log.info("Searching employees in team {} with request: {}", teamCode, request);

    validateTeamExists(teamCode);

    SearchTeamEmployeeRequest criteria = request.getCondition();
    if (criteria == null) {
      criteria = new SearchTeamEmployeeRequest();
    }
    criteria.setTeamCode(teamCode);

    int total = teamEmployeeRepository.count(criteria);
    if (total == CommonConstant.INIT_INDEX) {
      return PageResponse.empty();
    }

    int limit = request.getPageRequest().getPageSize();
    int offset = (request.getPageRequest().getPageNumber() - 1) * limit;

    List<SearchTeamEmployeeDto> result =
        teamEmployeeRepository.search(criteria, request.getSortRequests(), limit, offset);

    return PageResponse.of(result, total, offset, limit);
  }

  // ========================= HELPER METHODS =========================

  /**
   * Helper to process add employee logic and return DTO with full info. This reuses the main
   * addEmployeeToTeam logic but enriches the result.
   */
  private TeamEmployeeDto processAddEmployeeToTeam(Team team, TeamEmployeeAddRequest request) {
    String roleCode = determineRoleCode(request);

    // Call the main logic to add/reactivate record in DB
    TeamEmployee teamEmployee =
        addEmployeeToTeam(
            team.getTeamCode(), request.getEmployeeId(), roleCode, request.getIsLeader());

    // Build complete DTO using Team info (for departmentCode)
    return buildTeamEmployeeDto(teamEmployee, request.getEmployeeId(), team);
  }

  private Team validateTeamExists(String teamCode) {
    Team team = teamMapper.findByTeamCode(teamCode);
    if (team == null) {
      throw notFound(MessageConstant.E_TEAM_001);
    }
    return team;
  }

  private Employee validateEmployeeExists(String employeeId) {
    Employee employee = employeeMapper.findByClientId(employeeId);
    if (employee == null) {
      throw notFound(MessageConstant.W_EMP_002);
    }
    return employee;
  }

  private void validateEmployeeNotActiveInTeam(String teamCode, String employeeId) {
    if (Boolean.TRUE.equals(
        teamEmployeeRepository.existsByTeamCodeAndEmployeeId(teamCode, employeeId))) {
      throw badRequest(MessageConstant.E_TEAM_EMP_001);
    }
  }

  private void validateTeamLeadConstraint(String teamCode, Boolean isLeader) {
    if (Boolean.TRUE.equals(isLeader)
        && Boolean.TRUE.equals(teamEmployeeRepository.existsTeamLeadByTeamCode(teamCode))) {
      throw badRequest(MessageConstant.E_TEAM_006);
    }
  }

  private TeamEmployee reactivateEmployeeInTeam(
      String teamCode, String employeeId, String roleCode, Boolean isLeader) {
    validateTeamLeadConstraint(teamCode, isLeader);
    teamEmployeeRepository.reactivate(teamCode, employeeId, roleCode, isLeader);
    return teamEmployeeRepository.findByTeamCodeAndEmployeeId(teamCode, employeeId);
  }

  private TeamEmployee createNewTeamEmployee(
      String teamCode, String employeeId, String roleCode, Boolean isLeader) {

    validateTeamLeadConstraint(teamCode, isLeader);

    // Using Lombok Builder Pattern
    TeamEmployee teamEmployee =
        TeamEmployee.builder()
            .teamCode(teamCode)
            .employeeId(employeeId)
            .roleCode(roleCode)
            .isLeader(isLeader)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .deleteFlag(DeleteFlagEnums.NOT_DELETED.getValue().shortValue())
            .build();

    teamEmployeeRepository.insert(teamEmployee);
    return teamEmployee;
  }

  private String determineRoleCode(TeamEmployeeAddRequest request) {
    if (request.getRoleCode() != null && !request.getRoleCode().isEmpty()) {
      return request.getRoleCode();
    }
    return Boolean.TRUE.equals(request.getIsLeader())
        ? TeamRoleEnums.TEAM_LEAD.getRoleCode()
        : TeamRoleEnums.EMPLOYEE.getRoleCode();
  }

  private TeamEmployeeDto buildTeamEmployeeDto(
      TeamEmployee teamEmployee, String clientId, Team team) {
    Employee employee = validateEmployeeExists(clientId);
    return buildTeamEmployeeDto(teamEmployee, employee, team);
  }

  private TeamEmployeeDto buildTeamEmployeeDto(
      TeamEmployee teamEmployee, Employee employee, Team team) {
    return teamEmployeeConverter.toDto(teamEmployee, employee, team);
  }

  private TeamEmployeeDto buildTeamEmployeeDto(TeamEmployee teamEmployee, Employee employee) {
    return teamEmployeeConverter.entityToDto(teamEmployee);
  }

  private TeamEmployeeBatchResult.BatchError buildBatchError(String clientId, String errorMessage) {
    return TeamEmployeeBatchResult.BatchError.builder()
        .clientId(clientId)
        .errorCode(MessageConstant.S_TEAM_EMP_001)
        .errorMessage(errorMessage)
        .build();
  }
}
