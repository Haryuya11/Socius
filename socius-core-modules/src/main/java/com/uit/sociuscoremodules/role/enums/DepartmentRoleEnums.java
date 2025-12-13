package com.uit.sociuscoremodules.role.enums;

import lombok.Getter;

/** Enumeration representing department roles within the application. */
@Getter
public enum DepartmentRoleEnums {

  /** Department Director role. */
  DEPT_DIR("DEPT_DIR", "Department Director"),

  /** Department Manager role. */
  DEPT_MGR("DEPT_MGR", "Department Manager"),

  /** Department Member role. */
  DEPT_MEM("DEPT_MEM", "Department Member");

  private final String roleCode;
  private final String roleName;

  DepartmentRoleEnums(String roleCode, String roleName) {
    this.roleCode = roleCode;
    this.roleName = roleName;
  }
}
