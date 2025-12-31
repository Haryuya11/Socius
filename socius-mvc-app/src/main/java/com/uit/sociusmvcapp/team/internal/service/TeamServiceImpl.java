package com.uit.sociusmvcapp.team.internal.service;

import com.uit.sociusmvcapp.shared.constants.MessageConstant;
import com.uit.sociusmvcapp.shared.service.ExceptionFactory;
import com.uit.sociusmvcapp.team.TeamActionGuard;
import com.uit.sociusmvcapp.team.TeamService;
import com.uit.sociusmvcapp.team.dto.TeamDto;
import com.uit.sociusmvcapp.team.dto.request.TeamCreateRequest;
import com.uit.sociusmvcapp.team.dto.request.TeamUpdateRequest;
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
      throw ExceptionFactory.badRequest(MessageConstant.E_TEAM_002);
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
  public TeamDto update(String teamCode, TeamUpdateRequest request) {
    TeamDto team = teamRepository.findByTeamCode(teamCode);
    if (team == null) {
      throw ExceptionFactory.notFound(MessageConstant.E_TEAM_001);
    }

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
}
