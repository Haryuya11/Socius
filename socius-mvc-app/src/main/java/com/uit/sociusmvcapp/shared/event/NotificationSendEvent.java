package com.uit.sociusmvcapp.shared.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/** * Generic event for sending notifications across different modules. */
@Getter
public class NotificationSendEvent extends ApplicationEvent {
  private final String receiverId;
  private final String title;
  private final String content;
  private final String linkUrl;

  /** Constructor for NotificationSendEvent. */
  public NotificationSendEvent(
      Object source, String receiverId, String title, String content, String linkUrl) {
    super(source);
    this.receiverId = receiverId;
    this.title = title;
    this.content = content;
    this.linkUrl = linkUrl;
  }
}
