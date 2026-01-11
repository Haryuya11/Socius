package com.uit.sociusmvcapp.workforce.internal.constants;

/** Constants used in the team-employee module. */
public final class TeamEmployeeConstant {

  /** Maximum number of team leaders allowed in a team. */
  public static final int MAX_TEAM_LEADERS = 1;

  /** Indicates no team leaders exist. */
  public static final int NO_TEAM_LEADERS = 0;

  private TeamEmployeeConstant() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }
}
