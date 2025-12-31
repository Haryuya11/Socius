package com.uit.sociusmvcapp.notification.enums;

import lombok.Getter;

/** Enumeration for notification read status. */
@Getter
public enum NotificationStatus {

  /** Notification is unread. */
  UNREAD((short) 0, "UNREAD"),

  /** Notification has been read. */
  READ((short) 1, "READ");

  private final Short code;
  private final String description;

  NotificationStatus(Short code, String description) {
    this.code = code;
    this.description = description;
  }
}
