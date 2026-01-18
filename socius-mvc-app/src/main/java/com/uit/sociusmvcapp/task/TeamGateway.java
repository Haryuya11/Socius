package com.uit.sociusmvcapp.task;

/**
 * Gateway interface for team-related operations.
 *
 * <p>This interface allows Task module to validate teams without direct dependency on Team module,
 * following the Dependency Inversion Principle and maintaining clean module boundaries.
 */
public interface TeamGateway {

  /**
   * Validate if a team exists by its code.
   *
   * @param teamCode the team code to validate
   * @throws NotFoundException if team not found
   */
  void validateTeamExists(String teamCode);
}
