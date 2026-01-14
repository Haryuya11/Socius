package com.uit.sociusmvcapp.message.enums;

import lombok.Getter;

/** Enumeration for message types. */
@Getter
public enum MessageType {

  /** Text message. */
  TEXT("TEXT", "Text message"),

  /** Image message. */
  IMAGE("IMAGE", "Image message"),

  /** File attachment message. */
  FILE("FILE", "File attachment"),

  /** System generated message. */
  SYSTEM("SYSTEM", "System message");

  private final String code;
  private final String description;

  MessageType(String code, String description) {
    this.code = code;
    this.description = description;
  }
}
