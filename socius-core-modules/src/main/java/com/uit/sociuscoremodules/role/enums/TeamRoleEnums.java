package com.uit.sociuscoremodules.role.enums;

import lombok.Getter;

/** Enumeration representing team roles within the application. */
@Getter
public enum TeamRoleEnums {

  /** Team Leader role. */
  TEAM_LEAD("TEAM_LEAD", "Team Leader"),

  /** Team Member role. */
  TEAM_MEM("TEAM_MEM", "Team Member");

  private final String roleCode;
  private final String roleName;

  TeamRoleEnums(String roleCode, String roleName) {
    this.roleCode = roleCode;
    this.roleName = roleName;
  }
}
