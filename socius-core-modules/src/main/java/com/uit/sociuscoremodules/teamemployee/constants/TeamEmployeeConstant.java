package com.uit.sociuscoremodules.teamemployee.constants;

/** Constants used in the team-employee module. */
public class TeamEmployeeConstant {

  private TeamEmployeeConstant() {
    throw new IllegalStateException("Utility class");
  }

  /** Employee not found error code. */
  public static final String EMPLOYEE_NOT_FOUND = "employee.not.found";

  /** Employee already in team error code. */
  public static final String EMPLOYEE_ALREADY_IN_TEAM = "employee.already.in.team";

  /** Employee not in team error code. */
  public static final String EMPLOYEE_NOT_IN_TEAM = "employee.not.in.team";

  /** Team lead already exists error code. */
  public static final String TEAM_LEAD_ALREADY_EXISTS = "team.lead.already.exists";

  /** Cannot remove team lead error code. */
  public static final String CANNOT_REMOVE_TEAM_LEAD = "cannot.remove.team.lead";

  /** Team not found error code. */
  public static final String TEAM_NOT_FOUND = "team.not.found";

  /** Employee added to team successfully. */
  public static final String EMPLOYEE_ADDED = "team.employee.added";

  /** Employee removed from team successfully. */
  public static final String EMPLOYEE_REMOVED = "team.employee.removed";

  /** Team lead changed successfully. */
  public static final String TEAM_LEAD_CHANGED = "team.lead.changed";
}
