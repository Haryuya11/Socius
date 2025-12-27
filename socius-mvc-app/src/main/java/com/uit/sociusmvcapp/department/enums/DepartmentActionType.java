package com.uit.sociusmvcapp.department.enums;

import lombok.Getter;

/** Enumeration representing different types of actions that can be performed on a department. */
@Getter
public enum DepartmentActionType {
  /** Action to deactivate a department. */
  DEACTIVATE("DEACTIVATE", "Deactivate Department"),

  /** Action to activate a department. */
  ACTIVATE("ACTIVATE", "Activate Department"),

  UPDATE_INFO("UPDATE_INFO", "Update Department Information");

  private final String code;
  private final String description;

  DepartmentActionType(String code, String description) {
    this.code = code;
    this.description = description;
  }
}
