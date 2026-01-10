package com.uit.sociusmvcapp.team.internal.service;

import com.uit.sociusmvcapp.shared.constants.CommonConstant;
import com.uit.sociusmvcapp.shared.constants.MessageConstant;
import com.uit.sociusmvcapp.shared.request.PaginationSearchRequest;
import com.uit.sociusmvcapp.shared.response.PageResponse;
import com.uit.sociusmvcapp.shared.service.ExceptionFactory;
import com.uit.sociusmvcapp.team.DepartmentGateway;
import com.uit.sociusmvcapp.team.TeamActionGuard;
import com.uit.sociusmvcapp.team.TeamService;
import com.uit.sociusmvcapp.team.dto.SearchTeamDto;
import com.uit.sociusmvcapp.team.dto.TeamDto;
import com.uit.sociusmvcapp.team.dto.request.CreateTeamRequest;
import com.uit.sociusmvcapp.team.dto.request.SearchTeamRequest;
import com.uit.sociusmvcapp.team.dto.request.UpdateTeamRequest;
import com.uit.sociusmvcapp.team.enums.TeamActionType;
import com.uit.sociusmvcapp.team.internal.repository.TeamRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Implementation of TeamService for team management operations. */
@Slf4j
@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

  private final TeamRepository teamRepository;

  private final List<TeamActionGuard> guards;

  private final DepartmentGateway departmentGateway;

  // ========================= TEAM SERVICE MAIN METHODS =========================

  /**
   * Create a new team or reactivate a soft-deleted team.
   *
   * @param request the team creation request
   */
  @Override
  @Transactional
  public void create(CreateTeamRequest request) {
    TeamDto existingTeam = teamRepository.findByTeamCode(request.getTeamCode());
    if (existingTeam != null) {
      throw ExceptionFactory.badRequest(MessageConstant.E_TEAM_002);
    }

    departmentGateway.validateDepartmentExists(request.getDepartmentCode());

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
  public TeamDto findByTeamCode(String teamCode) {
    TeamDto team = teamRepository.findByTeamCode(teamCode);
    if (team == null) {
      throw ExceptionFactory.notFound(MessageConstant.E_TEAM_001);
    }
    return team;
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
  public TeamDto update(String teamCode, UpdateTeamRequest request) {
    TeamDto team = teamRepository.findByTeamCode(teamCode);
    if (team == null) {
      throw ExceptionFactory.notFound(MessageConstant.E_TEAM_001);
    }

    departmentGateway.validateDepartmentExists(request.getDepartmentCode());

    teamRepository.update(teamCode, request);
    return findByTeamCode(teamCode);
  }

  /**
   * Soft delete a team.
   *
   * @param teamCode the team code
   */
  @Override
  @Transactional
  public void delete(String teamCode) {
    this.validateExists(teamCode);

    for (TeamActionGuard guard : guards) {
      guard.validate(TeamActionType.DEACTIVATE, teamCode);
    }

    teamRepository.softDelete(teamCode);
  }

  /**
   * Validate if a team exists by its code.
   *
   * @param teamCode the code of the team
   */
  @Override
  public void validateExists(String teamCode) {
    boolean exists = teamRepository.existsByTeamCode(teamCode);
    if (!exists) {
      throw ExceptionFactory.notFound(MessageConstant.E_TEAM_001);
    }
  }

  /**
   * Search for teams based on given criteria with pagination.
   *
   * @param request the pagination search request containing search criteria
   * @return a paginated response of SearchTeamDto matching the search criteria
   */
  @Override
  public PageResponse<SearchTeamDto> search(PaginationSearchRequest<SearchTeamRequest> request) {
    int limit = request.getPageRequest().getPageSize();
    int offset = (request.getPageRequest().getPageNumber() - 1) * limit;
    SearchTeamRequest criteria = request.getCondition();

    int total = teamRepository.count(criteria);
    if (total == CommonConstant.INIT_INDEX) {
      log.info("No teams found matching the search criteria.");
      return PageResponse.empty();
    }

    List<SearchTeamDto> result =
        teamRepository.search(criteria, request.getSortRequests(), limit, offset);

    return PageResponse.of(result, total, offset, limit);
  }
}
