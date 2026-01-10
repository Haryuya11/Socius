package com.uit.sociusmvcapp.workforce.internal.component;

import com.uit.sociusmvcapp.shared.constants.MessageConstant;
import com.uit.sociusmvcapp.shared.service.ExceptionFactory;
import com.uit.sociusmvcapp.team.TeamActionGuard;
import com.uit.sociusmvcapp.team.enums.TeamActionType;
import com.uit.sociusmvcapp.workforce.internal.repository.TeamEmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/** Guard component to validate team actions in the Workforce module. */
@Component
@RequiredArgsConstructor
@Slf4j
public class WorkforceTeamGuard implements TeamActionGuard {

  private final TeamEmployeeRepository teamEmployeeRepository;

  /**
   * Validates the specified action for the given team code.
   *
   * @param action the action to be validated
   * @param teamCode the code of the team
   */
  @Override
  public void validate(TeamActionType action, String teamCode) {
    switch (action) {
      case DEACTIVATE -> {
        log.info("Validating DEACTIVATE action for team: {}", teamCode);
        if (teamEmployeeRepository.hasEmployees(teamCode)) {
          log.error("Cannot deactivate team {} because it has assigned employees.", teamCode);
          throw ExceptionFactory.badRequest(MessageConstant.E_TEAM_007);
        }
      }
      case ACTIVATE -> log.info("Validating ACTIVATE action for team: {}", teamCode);

      case UPDATE_INFO -> log.info("Validating UPDATE_INFO action for team: {}", teamCode);

      default -> log.warn("No validation implemented for action: {}", action);
    }
  }

  /**
   * Gets the module name associated with this guard.
   *
   * @return the module name
   */
  @Override
  public String getModuleName() {
    return "Workforce Module";
  }
}
