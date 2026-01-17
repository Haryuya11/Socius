package com.uit.sociusmvcapp.team.internal.gateway;

import com.uit.sociusmvcapp.task.TeamGateway;
import com.uit.sociusmvcapp.team.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Implementation of TeamGateway for Task module.
 *
 * <p>This adapter allows Task module to validate teams without direct coupling to Team module
 * internals, following Dependency Inversion Principle.
 */
@Component
@RequiredArgsConstructor
public class TeamGatewayImpl implements TeamGateway {

  private final TeamService teamService;

  @Override
  public void validateTeamExists(String teamCode) {
    teamService.validateExists(teamCode);
  }
}
