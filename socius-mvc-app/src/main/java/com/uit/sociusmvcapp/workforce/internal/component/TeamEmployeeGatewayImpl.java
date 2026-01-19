package com.uit.sociusmvcapp.workforce.internal.component;

import com.uit.sociusmvcapp.team.TeamEmployeeGateway;
import com.uit.sociusmvcapp.workforce.dto.TeamEmployeeDto;
import com.uit.sociusmvcapp.workforce.internal.repository.TeamEmployeeRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Implementation of TeamEmployeeGateway for Team module.
 *
 * <p>This component provides team-employee relationship services to Team module without creating
 * direct dependency on Workforce module internals.
 */
@Component
@RequiredArgsConstructor
public class TeamEmployeeGatewayImpl implements TeamEmployeeGateway {

  private final TeamEmployeeRepository teamEmployeeRepository;

  /**
   * Get all active member IDs in a team.
   *
   * @param teamCode the team code
   * @return list of active employee client IDs
   */
  @Override
  public List<String> getActiveMemberIds(String teamCode) {
    List<TeamEmployeeDto> activeMembers = teamEmployeeRepository.findEmployeesByTeamCode(teamCode);
    return activeMembers.stream().map(te -> te.getEmployee().getClientId()).toList();
  }
}
