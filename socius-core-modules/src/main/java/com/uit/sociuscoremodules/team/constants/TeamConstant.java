package com.uit.sociuscoremodules.team.constants;

/** Constants used in the team module. */
public class TeamConstant {

  private TeamConstant() {
    throw new IllegalStateException("Utility class");
  }

  /** Team not found error code. */
  public static final String TEAM_NOT_FOUND = "team.not.found";

  /** Team already exists error code. */
  public static final String TEAM_ALREADY_EXISTS = "team.already.exists";

  /** Team code required error code. */
  public static final String TEAM_CODE_REQUIRED = "team.code.required";

  /** Team name required error code. */
  public static final String TEAM_NAME_REQUIRED = "team.name.required";

  /** Team lead required error code. */
  public static final String TEAM_LEAD_REQUIRED = "team.lead.required";

  /** Team lead already exists error code. */
  public static final String TEAM_LEAD_ALREADY_EXISTS = "team.lead.already.exists";

  /** Employee not found error code. */
  public static final String EMPLOYEE_NOT_FOUND = "employee.not.found";

  /** Employee already in team error code. */
  public static final String EMPLOYEE_ALREADY_IN_TEAM = "employee.already.in.team";

  /** Employee not in team error code. */
  public static final String EMPLOYEE_NOT_IN_TEAM = "employee.not.in.team";

  /** Invalid sort field error code. */
  public static final String INVALID_SORT_FIELD = "team.invalid.sort.field";

  /** Cannot delete team with employees error code. */
  public static final String CANNOT_DELETE_TEAM_WITH_EMPLOYEES =
      "team.cannot.delete.with.employees";

  /** Team created successfully. */
  public static final String TEAM_CREATED = "team.created";

  /** Team found successfully. */
  public static final String TEAM_FOUND = "team.found";

  /** Teams found successfully. */
  public static final String TEAMS_FOUND = "teams.found";

  /** Team updated successfully. */
  public static final String TEAM_UPDATED = "team.updated";

  /** Team deleted successfully. */
  public static final String TEAM_DELETED = "team.deleted";
}
