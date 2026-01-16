package com.uit.sociusmvcapp.task.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** Enum representing the status of a task. */
@Getter
@RequiredArgsConstructor
public enum TaskStatus {
  /** Task is being worked on. */
  IN_PROGRESS(0, "In Progress"),

  /** Task is submitted and pending review. */
  PENDING(1, "Pending Review"),

  /** Task has been approved. */
  APPROVED(2, "Approved"),

  /** Task has been rejected. */
  REJECTED(3, "Rejected"),

  /** Task is past its due date. */
  OVERDUE(4, "Overdue"),

  /** Task has been cancelled. */
  CANCELLED(5, "Cancelled");

  private final int code;
  private final String label;

  /**
   * Get TaskStatus from code.
   *
   * @param code the status code
   * @return the corresponding TaskStatus
   * @throws IllegalArgumentException if code is invalid
   */
  public static TaskStatus fromCode(int code) {
    for (TaskStatus status : values()) {
      if (status.code == code) {
        return status;
      }
    }
    throw new IllegalArgumentException("Invalid TaskStatus code: " + code);
  }
}
