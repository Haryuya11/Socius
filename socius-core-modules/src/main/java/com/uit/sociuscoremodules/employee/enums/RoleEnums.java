package com.uit.sociuscoremodules.employee.enums;

import lombok.Getter;

/** Enumeration representing different roles within the organization. */
@Getter
public enum RoleEnums {

  /** Administrator role. */
  ADMIN("ADM", "Administrator"),

  /** Staff role. */
  STAFF("STF", "Staff"),

  /** Customer role. */
  CUSTOMER("CST", "Customer");

  private final String roleCode;
  private final String roleName;

  RoleEnums(String roleCode, String roleName) {
    this.roleCode = roleCode;
    this.roleName = roleName;
  }
}
