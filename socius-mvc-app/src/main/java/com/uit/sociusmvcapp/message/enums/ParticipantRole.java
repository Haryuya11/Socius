package com.uit.sociusmvcapp.message.enums;

import lombok.Getter;

/** Enumeration for participant roles in a conversation. */
@Getter
public enum ParticipantRole {

  /** Owner/creator of the conversation. */
  OWNER("OWNER", "Conversation owner"),

  /** Administrator of the conversation. */
  ADMIN("ADMIN", "Conversation admin"),

  /** Regular member of the conversation. */
  MEMBER("MEMBER", "Conversation member");

  private final String code;
  private final String description;

  ParticipantRole(String code, String description) {
    this.code = code;
    this.description = description;
  }
}
