package com.uit.sociusmvcapp.shared.enums;

import lombok.Getter;

/** Enumeration for delete flag status. */
@Getter
public enum DeleteFlagEnums {

  /** Indicates the entity is not deleted. */
  NOT_DELETED(0, "Not Deleted"),

  /** Indicates the entity is deleted. */
  DELETED(1, "Deleted");

  private final Integer value;
  private final String description;

  DeleteFlagEnums(Integer value, String description) {
    this.value = value;
    this.description = description;
  }
}
