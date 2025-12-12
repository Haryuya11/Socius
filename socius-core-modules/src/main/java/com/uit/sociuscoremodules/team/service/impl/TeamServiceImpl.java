package com.uit.sociuscoremodules.team.service.impl;

import com.uit.sociuscoremodules.employee.domain.Employee;
import com.uit.sociuscoremodules.employee.persistence.EmployeeMapper;
import com.uit.sociuscoremodules.shared.constants.CommonConstant;
import com.uit.sociuscoremodules.shared.constants.MessageConstant;
import com.uit.sociuscoremodules.shared.request.PaginationSearchRequest;
import com.uit.sociuscoremodules.shared.response.PageResponse;
import com.uit.sociuscoremodules.shared.service.impl.BaseServiceImpl;
import com.uit.sociuscoremodules.team.converter.TeamConverter;
import com.uit.sociuscoremodules.team.dto.SearchTeamDto;
import com.uit.sociuscoremodules.team.dto.TeamDto;
import com.uit.sociuscoremodules.team.repository.TeamRepository;
import com.uit.sociuscoremodules.team.request.TeamCreateRequest;
import com.uit.sociuscoremodules.team.request.TeamSearchRequest;
import com.uit.sociuscoremodules.team.request.TeamUpdateRequest;
import com.uit.sociuscoremodules.team.service.TeamService;
import com.uit.sociuscoremodules.teamemployee.enums.TeamRoleEnums;
import com.uit.sociuscoremodules.teamemployee.repository.TeamEmployeeRepository;
import com.uit.sociuscoremodules.teamemployee.service.TeamEmployeeService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** Implementation of TeamService for team management operations. */
@Slf4j
@Service
@RequiredArgsConstructor
public class TeamServiceImpl extends BaseServiceImpl implements TeamService {

  private final TeamRepository teamRepository;
  private final TeamConverter teamConverter;
  private final EmployeeMapper employeeMapper;
  private final TeamEmployeeService teamEmployeeService;
  private final TeamEmployeeRepository teamEmployeeRepository;

  // ========================= TEAM SERVICE MAIN METHODS =========================

  /**
   * Create a new team or reactivate a soft-deleted team.
   *
   * @param request the team creation request
   */
  @Override
  public void createTeam(TeamCreateRequest request) {
    TeamDto existingTeam = teamRepository.findByTeamCode(request.getTeamCode());
    if (existingTeam != null) {
      throw badRequest(MessageConstant.E_TEAM_002);
    }

    TeamDto deletedTeam = teamRepository.findDeletedByTeamCode(request.getTeamCode());
    if (deletedTeam != null) {
      teamRepository.reactivateTeam(request);
    } else {
      teamRepository.insert(request);
    }
  }

  /**
   * Retrieve team details by team code.
   *
   * @param teamCode the team code
   * @return TeamDto with details
   */
  @Override
  public TeamDto getTeamByTeamCode(String teamCode) {
    TeamDto team = teamRepository.findByTeamCode(teamCode);
    if (team == null) {
      throw notFound(MessageConstant.E_TEAM_001);
    }
    return team;
  }

  /**
   * Search teams with filtering and pagination.
   *
   * @param request the search request
   * @return PageResponse of SearchTeamDto
   */
  @Override
  public PageResponse<SearchTeamDto> searchTeams(
      PaginationSearchRequest<TeamSearchRequest> request) {

    TeamSearchRequest criteria = request.getCondition();
    if (criteria == null) {
      criteria = new TeamSearchRequest();
    }

    int total = teamRepository.count(criteria);
    if (total == CommonConstant.INIT_INDEX) {
      return PageResponse.empty();
    }

    int limit = request.getPageRequest().getPageSize();
    int offset = (request.getPageRequest().getPageNumber() - 1) * limit;

    List<SearchTeamDto> result =
        teamRepository.search(criteria, request.getSortRequests(), limit, offset);

    return PageResponse.of(result, total, offset, limit);
  }

  /**
   * Update team information.
   *
   * @param teamCode the team code
   * @param request the update request
   * @return the updated TeamDto
   */
  @Override
  public TeamDto updateTeam(String teamCode, TeamUpdateRequest request) {
    TeamDto team = teamRepository.findByTeamCode(teamCode);
    if (team == null) {
      throw notFound(MessageConstant.E_TEAM_001);
    }

    if (request.getTeamName() != null && !request.getTeamName().isEmpty()) {
      team.setTeamName(request.getTeamName());
    }
    teamRepository.update(request);
    return getTeamByTeamCode(teamCode);
  }

  /**
   * Soft delete a team.
   *
   * @param teamCode the team code
   */
  @Override
  public void deleteTeam(String teamCode) {
    TeamDto team = teamRepository.findByTeamCode(teamCode);
    if (team == null) {
      throw notFound(MessageConstant.E_TEAM_001);
    }

    // Check if team has any employees before deleting
    List<Employee> employees = teamEmployeeService.getEmployeesByTeamCode(teamCode);
    if (!employees.isEmpty()) {
      throw badRequest(MessageConstant.E_TEAM_007);
    }

    teamRepository.softDelete(team.getTeamCode());
  }

  // ========================= HELPER METHODS =========================
  /** Add team lead if they are not already in the team. */
  private void addTeamLeadIfMissing(String teamCode, String leadClientId) {
    Employee teamLead = validateAndGetTeamLead(leadClientId);

    if (!Boolean.TRUE.equals(
        teamEmployeeRepository.existsByTeamCodeAndEmployeeId(teamCode, teamLead.getClientId()))) {
      teamEmployeeService.addEmployeeToTeam(
          teamCode, teamLead.getClientId(), TeamRoleEnums.TEAM_LEAD.getRoleCode(), true);
    }
  }

  /** Validate if team lead exists. */
  private Employee validateAndGetTeamLead(String clientId) {
    Employee teamLead = employeeMapper.findByClientId(clientId);
    if (teamLead == null) {
      throw notFound(MessageConstant.W_EMP_002);
    }
    return teamLead;
  }
}
