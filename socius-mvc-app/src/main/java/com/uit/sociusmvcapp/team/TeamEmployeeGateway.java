package com.uit.sociusmvcapp.team;

import java.util.List;

/**
 * Gateway interface for team-employee relationship operations.
 *
 * <p>This interface allows Team module to access team member information without direct dependency
 * on Workforce module, following the Dependency Inversion Principle and maintaining clean module
 * boundaries.
 */
public interface TeamEmployeeGateway {

  /**
   * Get all active member IDs in a team.
   *
   * <p>Returns only active (non-deleted) employee client IDs for notification purposes.
   *
   * @param teamCode the team code
   * @return list of active employee client IDs
   */
  List<String> getActiveMemberIds(String teamCode);
}
