package com.uit.sociusmvcapp.team.internal.gateway;

import com.uit.sociusmvcapp.iam.TeamInfoGateway;
import com.uit.sociusmvcapp.shared.exception.BusinessException;
import com.uit.sociusmvcapp.task.TeamGateway;
import com.uit.sociusmvcapp.team.TeamService;
import com.uit.sociusmvcapp.team.dto.TeamDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Implementation of TeamGateway for Task module and TeamInfoGateway for IAM module.
 *
 * <p>This adapter allows Task and IAM modules to access team information without direct coupling to
 * Team module internals, following Dependency Inversion Principle.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TeamGatewayImpl implements TeamGateway, TeamInfoGateway {

  private final TeamService teamService;

  @Override
  public void validateTeamExists(String teamCode) {
    teamService.validateExists(teamCode);
  }

  /**
   * Get the department code for a team.
   *
   * <p>Note: This method is called during authorization checks. The team-to-department relationship
   * is stable and infrequently changed, so additional caching is not needed beyond what the
   * database layer provides.
   *
   * @param teamCode the team code
   * @return the department code that the team belongs to, or null if team not found
   */
  @Override
  public String getDepartmentCodeByTeamCode(String teamCode) {
    try {
      TeamDto team = teamService.findByTeamCode(teamCode);
      return team != null ? team.getDepartmentCode() : null;
    } catch (BusinessException e) {
      // Team not found - this is expected for invalid team codes
      log.debug("Could not find team [{}]: {}", teamCode, e.getMessage());
      return null;
    }
  }
}
