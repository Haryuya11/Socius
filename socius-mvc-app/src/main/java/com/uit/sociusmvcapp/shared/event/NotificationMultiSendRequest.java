package com.uit.sociusmvcapp.shared.event;

import java.util.List;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/** Generic event for sending notifications to multiple receivers across different modules. */
@Getter
public class NotificationMultiSendRequest extends ApplicationEvent {
  private final List<String> receiverIds;
  private final String title;
  private final String content;
  private final String linkUrl;

  /** Constructor for NotificationMultiSendRequest. */
  public NotificationMultiSendRequest(
      Object source, List<String> receiverIds, String title, String content, String linkUrl) {
    super(source);
    this.receiverIds = receiverIds;
    this.title = title;
    this.content = content;
    this.linkUrl = linkUrl;
  }
}
