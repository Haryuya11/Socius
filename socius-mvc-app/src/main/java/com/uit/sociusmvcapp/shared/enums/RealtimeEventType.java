package com.uit.sociusmvcapp.shared.enums;

import lombok.Getter;

/** Enumeration for realtime event types. */
@Getter
public enum RealtimeEventType {

  // Message events
  /** A new message was sent. */
  NEW_MESSAGE("NEW_MESSAGE", "New message event"),

  /** A message was updated/edited. */
  MESSAGE_UPDATED("MESSAGE_UPDATED", "Message updated event"),

  /** A message was deleted. */
  MESSAGE_DELETED("MESSAGE_DELETED", "Message deleted event"),

  /** User is typing indicator. */
  TYPING_INDICATOR("TYPING_INDICATOR", "Typing indicator event"),

  /** A reaction was added to a message. */
  REACTION_ADDED("REACTION_ADDED", "Reaction added event"),

  /** A reaction was removed from a message. */
  REACTION_REMOVED("REACTION_REMOVED", "Reaction removed event"),

  // Notification events
  /** A new notification was created. */
  NEW_NOTIFICATION("NEW_NOTIFICATION", "New notification event"),

  // System events
  /** System broadcast event. */
  SYSTEM_BROADCAST("SYSTEM_BROADCAST", "System broadcast event");

  private final String code;
  private final String description;

  RealtimeEventType(String code, String description) {
    this.code = code;
    this.description = description;
  }
}
