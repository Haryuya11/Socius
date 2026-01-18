package com.uit.sociusmvcapp.shared.enums;

import lombok.Getter;

/** Enumeration for realtime event domains. */
@Getter
public enum RealtimeDomain {

  /** Message-related events. */
  MESSAGE("MESSAGE", "Message domain"),

  /** Notification-related events. */
  NOTIFICATION("NOTIFICATION", "Notification domain"),

  /** System-related events. */
  SYSTEM("SYSTEM", "System domain");

  private final String code;
  private final String description;

  RealtimeDomain(String code, String description) {
    this.code = code;
    this.description = description;
  }
}
