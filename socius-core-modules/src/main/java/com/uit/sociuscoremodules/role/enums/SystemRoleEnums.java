package com.uit.sociuscoremodules.role.enums;

import lombok.Getter;

/** Enumeration representing system roles within the application. */
@Getter
public enum SystemRoleEnums {

  /** Administrator role. */
  SYS_ADMIN("SYS_ADMIN", "System Administrator"),

  /** User role. */
  USER("USER", "User");

  private final String roleCode;
  private final String roleName;

  SystemRoleEnums(String roleCode, String roleName) {
    this.roleCode = roleCode;
    this.roleName = roleName;
  }
}
