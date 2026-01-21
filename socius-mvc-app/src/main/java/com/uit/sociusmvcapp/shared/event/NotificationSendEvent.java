package com.uit.sociusmvcapp.shared.event;

import java.util.Map;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/** * Generic event for sending notifications across different modules. */
@Getter
public class NotificationSendEvent extends ApplicationEvent {
  private final String receiverId;
  private final String title;
  private final String content;
  private final Map<String, String> parameters;
  private final String linkUrl;

  /** Constructor for NotificationSendEvent with i18n support. */
  public NotificationSendEvent(
      Object source,
      String receiverId,
      String title,
      String content,
      Map<String, String> parameters,
      String linkUrl) {
    super(source);
    this.receiverId = receiverId;
    this.title = title;
    this.content = content;
    this.parameters = parameters;
    this.linkUrl = linkUrl;
  }

  /** Constructor for NotificationSendEvent without parameters (backward compatibility). */
  public NotificationSendEvent(
      Object source, String receiverId, String title, String content, String linkUrl) {
    this(source, receiverId, title, content, null, linkUrl);
  }
}
