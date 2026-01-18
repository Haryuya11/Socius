package com.uit.sociusmvcapp.message.enums;

import lombok.Getter;

/** Enumeration for conversation types. */
@Getter
public enum ConversationType {

  /** Direct message between two users. */
  DIRECT("DIRECT", "Direct message"),

  /** Group conversation with multiple participants. */
  GROUP("GROUP", "Group conversation");

  private final String code;
  private final String description;

  ConversationType(String code, String description) {
    this.code = code;
    this.description = description;
  }
}
