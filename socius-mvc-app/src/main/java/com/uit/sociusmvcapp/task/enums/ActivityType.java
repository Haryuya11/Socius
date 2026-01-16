package com.uit.sociusmvcapp.task.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** Enum representing the type of task activity. */
@Getter
@RequiredArgsConstructor
public enum ActivityType {
  /** Task was submitted for review. */
  SUBMIT(0, "Submit Review"),

  /** Task was approved. */
  APPROVE(1, "Approve"),

  /** Task was rejected. */
  REJECT(2, "Reject"),

  /** Task was cancelled. */
  CANCEL(3, "Cancel"),

  /** Task was reopened. */
  REOPEN(4, "Reopen"),

  /** Task was created. */
  CREATE(5, "Create"),

  /** Task was updated. */
  UPDATE(6, "Update");

  private final int code;
  private final String label;

  /**
   * Get ActivityType from code.
   *
   * @param code the activity type code
   * @return the corresponding ActivityType
   * @throws IllegalArgumentException if code is invalid
   */
  public static ActivityType fromCode(int code) {
    for (ActivityType type : values()) {
      if (type.code == code) {
        return type;
      }
    }
    throw new IllegalArgumentException("Invalid ActivityType code: " + code);
  }
}
