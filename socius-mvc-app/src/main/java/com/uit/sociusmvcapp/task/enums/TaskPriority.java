package com.uit.sociusmvcapp.task.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** Enum representing the priority level of a task. */
@Getter
@RequiredArgsConstructor
public enum TaskPriority {
  /** Low priority task. */
  LOW(0, "Low"),

  /** Medium priority task. */
  MEDIUM(1, "Medium"),

  /** High priority task. */
  HIGH(2, "High");

  private final int code;
  private final String label;

  /**
   * Get TaskPriority from code.
   *
   * @param code the priority code
   * @return the corresponding TaskPriority
   * @throws IllegalArgumentException if code is invalid
   */
  public static TaskPriority fromCode(int code) {
    for (TaskPriority priority : values()) {
      if (priority.code == code) {
        return priority;
      }
    }
    throw new IllegalArgumentException("Invalid TaskPriority code: " + code);
  }
}
