package com.uit.sociusmvcapp.team;

import com.uit.sociusmvcapp.team.enums.TeamActionType;

/** Guard interface for validating actions on teams. */
public interface TeamActionGuard {
  /**
   * Validates whether the specified action can be performed on the team.
   *
   * @param action the action to be validated
   * @param teamCode the code of the team
   */
  void validate(TeamActionType action, String teamCode);

  /**
   * Gets the module name associated with this guard.
   *
   * @return the module name
   */
  default String getModuleName() {
    return "Team Module";
  }
}
