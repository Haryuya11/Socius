package com.uit.sociusmvcapp.team.internal.service;

import com.uit.sociusmvcapp.iam.UserContentProvider;
import com.uit.sociusmvcapp.shared.constants.CommonConstant;
import com.uit.sociusmvcapp.shared.constants.MessageConstant;
import com.uit.sociusmvcapp.shared.event.NotificationMultiSendRequest;
import com.uit.sociusmvcapp.shared.event.NotificationSendEvent;
import com.uit.sociusmvcapp.shared.request.PaginationSearchRequest;
import com.uit.sociusmvcapp.shared.response.PageResponse;
import com.uit.sociusmvcapp.shared.service.ExceptionFactory;
import com.uit.sociusmvcapp.team.DepartmentGateway;
import com.uit.sociusmvcapp.team.TeamActionGuard;
import com.uit.sociusmvcapp.team.TeamEmployeeGateway;
import com.uit.sociusmvcapp.team.TeamService;
import com.uit.sociusmvcapp.team.dto.SearchTeamDto;
import com.uit.sociusmvcapp.team.dto.TeamDto;
import com.uit.sociusmvcapp.team.dto.request.CreateTeamRequest;
import com.uit.sociusmvcapp.team.dto.request.SearchTeamRequest;
import com.uit.sociusmvcapp.team.dto.request.UpdateTeamRequest;
import com.uit.sociusmvcapp.team.enums.TeamActionType;
import com.uit.sociusmvcapp.team.internal.repository.TeamRepository;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Implementation of TeamService for team management operations. */
@Slf4j
@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

  private static final String TEAMS_PATH = "/teams/";

  private final TeamRepository teamRepository;

  private final List<TeamActionGuard> guards;

  private final DepartmentGateway departmentGateway;

  private final TeamEmployeeGateway teamEmployeeGateway;

  private final ApplicationEventPublisher eventPublisher;

  private final UserContentProvider userContentProvider;

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

    Map<String, String> params =
        Map.of(
            "TEAM_NAME", request.getTeamName(),
            "TEAM_CODE", request.getTeamCode());
    eventPublisher.publishEvent(
        new NotificationSendEvent(
            this,
            userContentProvider.getUserContent().getClientId(),
            "S_TEAM_TITLE_001",
            "S_TEAM_CONTENT_001",
            params,
            TEAMS_PATH + request.getTeamCode()));
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

    // Send batch notification to all team members about the update (avoid N+1)
    List<String> memberIds = getTeamMemberIds(teamCode);
    if (!memberIds.isEmpty()) {
      Map<String, String> params =
          Map.of("TEAM_NAME", request.getTeamName(), "TEAM_CODE", teamCode);
      eventPublisher.publishEvent(
          new NotificationMultiSendRequest(
              this,
              memberIds,
              "S_TEAM_TITLE_002",
              "S_TEAM_CONTENT_002",
              params,
              TEAMS_PATH + teamCode));
    }

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

    TeamDto team = teamRepository.findByTeamCode(teamCode);
    teamRepository.softDelete(teamCode);

    // Send batch notification to all team members about the deletion (avoid N+1)
    Map<String, String> params = Map.of("TEAM_NAME", team.getTeamName(), "TEAM_CODE", teamCode);
    eventPublisher.publishEvent(
        new NotificationSendEvent(
            this,
            userContentProvider.getUserContent().getClientId(),
            "S_TEAM_TITLE_003",
            "S_TEAM_CONTENT_003",
            params,
            "/teams"));
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

  // ========================= HELPER METHODS =========================

  /**
   * Get all active member IDs in a team for notification purposes.
   *
   * <p>Uses TeamEmployeeGateway to avoid circular dependency with TeamEmployee module.
   *
   * @param teamCode the team code
   * @return list of employee client IDs (only active members)
   */
  private List<String> getTeamMemberIds(String teamCode) {
    return teamEmployeeGateway.getActiveMemberIds(teamCode);
  }
}
