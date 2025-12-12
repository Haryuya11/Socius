package com.uit.sociuscoremodules.shared.constants;

/** Message constant for i18n keys. */
public final class MessageConstant {
  /** Private constructor to prevent instantiation. */
  private MessageConstant() {}

  /** General unexpected error message key. */
  public static final String E_SYS_001 = "E_SYS_001";

  /** Employee creation error message key. */
  public static final String E_EMP_001 = "E_EMP_001";

  /** Employee update error message key. */
  public static final String E_EMP_002 = "E_EMP_002";

  /** Employee deletion error message key. */
  public static final String E_EMP_003 = "E_EMP_003";

  /** Employee find error message key. */
  public static final String E_EMP_004 = "E_EMP_004";

  /** Employee reactivation error message key. */
  public static final String E_EMP_005 = "E_EMP_005";

  /** Employee change password error message key. */
  public static final String E_EMP_006 = "E_EMP_006";

  /** Employee is already exists error message key. */
  public static final String E_EMP_007 = "E_EMP_007";

  /** Employee created successfully. */
  public static final String S_EMP_001 = "S_EMP_001";

  /** Employee updated successfully. */
  public static final String S_EMP_002 = "S_EMP_002";

  /** Employee deleted successfully. */
  public static final String S_EMP_003 = "S_EMP_003";

  /** Employee details retrieved successfully. */
  public static final String S_EMP_004 = "S_EMP_004";

  /** Employee change password successfully. */
  public static final String S_EMP_005 = "S_EMP_005";

  /** Employee profile retrieved successfully. */
  public static final String S_EMP_006 = "S_EMP_006";

  /** Employees list reactivated successfully. */
  public static final String S_EMP_007 = "S_EMP_007";

  /** Employee with the same ID already exists. */
  public static final String W_EMP_001 = "W_EMP_001";

  /** Employee not found. */
  public static final String W_EMP_002 = "W_EMP_002";

  /** Failed to publish notification. */
  public static final String E_NOTIFY_001 = "E_NOTIFY_001";

  /** Successfully retrieved count of unread notifications. */
  public static final String S_NOTIFY_001 = "S_NOTIFY_001";
  
  /** Successfully retrieved roles list. */
  public static final String S_ROLE_001 = "S_ROLE_001";

  /** Successfully retrieved role details. */
  public static final String S_ROLE_002 = "S_ROLE_002";

  // --- Department Module Messages ---

  /** Department creation error message key. */
  public static final String E_DEP_001 = "E_DEP_001";

  /** Department update error message key. */
  public static final String E_DEP_002 = "E_DEP_002";

  /** Department find error message key. */
  public static final String E_DEP_003 = "E_DEP_003";

  /** Department deletion error message key. */
  public static final String E_DEP_004 = "E_DEP_004";

  /** Department is already exists error message key. */
  public static final String E_DEP_005 = "E_DEP_005";

  /** Cannot change manager. Employee not found. */
  public static final String E_DEP_006 = "E_DEP_006";

  /** Cannot deactivate. Department still has active employees. */
  public static final String E_DEP_007 = "E_DEP_007";

  /** Employee not found in the source department. */
  public static final String E_DEP_008 = "E_DEP_008";

  /** Source or target department not found for transfer. */
  public static final String E_DEP_009 = "E_DEP_009";

  /** Cannot find employee in department. */
  public static final String E_DEP_010 = "E_DEP_010";

  /** Cannot add employee to department. Employee already exists in department. */
  public static final String E_DEP_011 = "E_DEP_011";

  /** Department created successfully. */
  public static final String S_DEP_001 = "S_DEP_001";

  /** Department updated successfully. */
  public static final String S_DEP_002 = "S_DEP_002";

  /** Department details retrieved successfully. */
  public static final String S_DEP_003 = "S_DEP_003";

  /** Department deleted successfully. */
  public static final String S_DEP_004 = "S_DEP_004";

  /** Departments list retrieved successfully. */
  public static final String S_DEP_005 = "S_DEP_005";

  /** Department not found. */
  public static final String W_DEP_001 = "W_DEP_001";
  
  // ================= TEAM MODULE =================
  /** Team not found. */
  public static final String E_TEAM_001 = "E_TEAM_001";

  /** Team with this code already exists. */
  public static final String E_TEAM_002 = "E_TEAM_002";

  /** Team code is required. */
  public static final String E_TEAM_003 = "E_TEAM_003";

  /** Team name is required. */
  public static final String E_TEAM_004 = "E_TEAM_004";

  /** Team lead is required. */
  public static final String E_TEAM_005 = "E_TEAM_005";

  /** Team already has a team lead. */
  public static final String E_TEAM_006 = "E_TEAM_006";

  /** Cannot delete team that has employees. */
  public static final String E_TEAM_007 = "E_TEAM_007";

  /** Invalid sort field provided. */
  public static final String E_TEAM_008 = "E_TEAM_008";

  /** Team created successfully. */
  public static final String S_TEAM_001 = "S_TEAM_001";

  /** Team retrieved successfully. */
  public static final String S_TEAM_002 = "S_TEAM_002";

  /** Teams list retrieved successfully. */
  public static final String S_TEAM_003 = "S_TEAM_003";

  /** Team updated successfully. */
  public static final String S_TEAM_004 = "S_TEAM_004";

  /** Team deleted successfully. */
  public static final String S_TEAM_005 = "S_TEAM_005";

  // ================= TEAM EMPLOYEE MODULE =================
  /** Employee is already in the team. */
  public static final String E_TEAM_EMP_001 = "E_TEAM_EMP_001";

  /** Employee is not in the team. */
  public static final String E_TEAM_EMP_002 = "E_TEAM_EMP_002";

  /** Cannot remove team lead. */
  public static final String E_TEAM_EMP_003 = "E_TEAM_EMP_003";

  /** Employee not found (Team Context). */
  public static final String E_TEAM_EMP_004 = "E_TEAM_EMP_004";

  /** Employee added to team successfully. */
  public static final String S_TEAM_EMP_001 = "S_TEAM_EMP_001";

  /** Employee removed from team successfully. */
  public static final String S_TEAM_EMP_002 = "S_TEAM_EMP_002";

  /** Team lead changed successfully. */
  public static final String S_TEAM_EMP_003 = "S_TEAM_EMP_003";

  /** Team employees retrieved successfully. */
  public static final String S_TEAM_EMP_004 = "S_TEAM_EMP_004";

  /** Team employees batch add result. */
  public static final String S_TEAM_EMP_005 = "S_TEAM_EMP_005";
}
