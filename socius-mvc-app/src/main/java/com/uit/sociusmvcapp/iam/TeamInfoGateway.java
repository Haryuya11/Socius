package com.uit.sociusmvcapp.iam;

/**
 * Gateway interface for team information.
 *
 * <p>This interface allows IAM module to query team information without direct dependency on Team
 * module, following the Dependency Inversion Principle and maintaining clean module boundaries.
 */
public interface TeamInfoGateway {

  /**
   * Get the department code for a team.
   *
   * @param teamCode the team code
   * @return the department code that the team belongs to, or null if team not found
   */
  String getDepartmentCodeByTeamCode(String teamCode);
}
