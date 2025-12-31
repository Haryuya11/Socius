package com.uit.sociusmvcapp.team.enums;

import lombok.Getter;

/** Enumeration of team action types. */
@Getter
public enum TeamActionType {
  /** Deactivate a team. */
  DEACTIVATE("DEACTIVATE", "Deactivate Team"),

  /** Activate a team. */
  ACTIVATE("ACTIVATE", "Activate Team"),

  /** Update team information. */
  UPDATE_INFO("UPDATE_INFO", "Update Team Information");

  private final String code;
  private final String description;

  TeamActionType(String code, String description) {
    this.code = code;
    this.description = description;
  }
}
