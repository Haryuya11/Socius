package com.uit.sociuscoremodules.notification.enums;

import lombok.Getter;

/** Enumeration for different types of notifications. */
@Getter
public enum DeliveryType {

  /** Email notification type. */
  EMAIL((short) 1, "EMAIL"),

  /** General notification type. */
  NOTIFICATION((short) 2, "NOTIFICATION");

  private final Short code;
  private final String description;

  DeliveryType(Short code, String description) {
    this.code = code;
    this.description = description;
  }
}
