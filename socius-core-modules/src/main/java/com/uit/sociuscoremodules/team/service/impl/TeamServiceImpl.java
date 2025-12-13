package com.uit.sociuscoremodules.team.service.impl;

import com.uit.sociuscoremodules.employee.dto.EmployeeDto;
import com.uit.sociuscoremodules.shared.constants.CommonConstant;
import com.uit.sociuscoremodules.shared.constants.MessageConstant;
import com.uit.sociuscoremodules.shared.request.PaginationSearchRequest;
import com.uit.sociuscoremodules.shared.response.PageResponse;
import com.uit.sociuscoremodules.shared.service.impl.BaseServiceImpl;
import com.uit.sociuscoremodules.team.dto.SearchTeamDto;
import com.uit.sociuscoremodules.team.dto.TeamDto;
import com.uit.sociuscoremodules.team.repository.TeamRepository;
import com.uit.sociuscoremodules.team.request.TeamCreateRequest;
import com.uit.sociuscoremodules.team.request.TeamSearchRequest;
import com.uit.sociuscoremodules.team.request.TeamUpdateRequest;
import com.uit.sociuscoremodules.team.service.TeamService;
import com.uit.sociuscoremodules.teamemployee.service.TeamEmployeeService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Implementation of TeamService for team management operations. */
@Slf4j
@Service
@RequiredArgsConstructor
public class TeamServiceImpl extends BaseServiceImpl implements TeamService {

  private final TeamRepository teamRepository;
  private final TeamEmployeeService teamEmployeeService;

  // ========================= TEAM SERVICE MAIN METHODS =========================

  /**
   * Create a new team or reactivate a soft-deleted team.
   *
   * @param request the team creation request
   */
  @Override
  @Transactional
  public void create(TeamCreateRequest request) {
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
  @Transactional
  public TeamDto updateTeam(String teamCode, TeamUpdateRequest request) {
    TeamDto team = teamRepository.findByTeamCode(teamCode);
    if (team == null) {
      throw notFound(MessageConstant.E_TEAM_001);
    }

    teamRepository.update(teamCode, request);
    return getTeamByTeamCode(teamCode);
  }

  /**
   * Soft delete a team.
   *
   * @param teamCode the team code
   */
  @Override
  @Transactional
  public void deleteTeam(String teamCode) {
    TeamDto team = teamRepository.findByTeamCode(teamCode);
    if (team == null) {
      throw notFound(MessageConstant.E_TEAM_001);
    }

    List<EmployeeDto> teamMembers = teamEmployeeService.getEmployeesByTeamCode(teamCode);
    if (teamMembers != null && !teamMembers.isEmpty()) {
      throw badRequest(MessageConstant.E_TEAM_007);
    }

    teamRepository.softDelete(teamCode);
  }
}
