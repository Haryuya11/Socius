package com.uit.sociuscoremodules.teamemployee.enums;

import lombok.Getter;

/** Enumeration representing different roles within a team. */
@Getter
public enum TeamRoleEnums {

  /** Team Leader role. */
  TEAM_LEAD("TEAM_LEAD", "Team Leader"),

  /** Team Member role. */
  EMPLOYEE("TEAM_MEM", "Team Member");

  private final String roleCode;
  private final String roleName;

  TeamRoleEnums(String roleCode, String roleName) {
    this.roleCode = roleCode;
    this.roleName = roleName;
  }

  /**
   * Get TeamRoleEnums by role code.
   *
   * @param roleCode the role code
   * @return the corresponding TeamRoleEnums
   */
  public static TeamRoleEnums fromRoleCode(String roleCode) {
    for (TeamRoleEnums role : values()) {
      if (role.roleCode.equals(roleCode)) {
        return role;
      }
    }
    throw new IllegalArgumentException("Invalid team role code: " + roleCode);
  }
}
