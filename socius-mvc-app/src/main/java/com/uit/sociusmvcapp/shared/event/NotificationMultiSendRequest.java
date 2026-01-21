package com.uit.sociusmvcapp.shared.event;

import java.util.List;
import java.util.Map;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/** Generic event for sending notifications to multiple receivers across different modules. */
@Getter
public class NotificationMultiSendRequest extends ApplicationEvent {
  private final List<String> receiverIds;
  private final String title;
  private final String content;
  private final Map<String, String> parameters;
  private final String linkUrl;

  /** Constructor for NotificationMultiSendRequest with i18n support. */
  public NotificationMultiSendRequest(
      Object source,
      List<String> receiverIds,
      String title,
      String content,
      Map<String, String> parameters,
      String linkUrl) {
    super(source);
    this.receiverIds = receiverIds;
    this.title = title;
    this.content = content;
    this.parameters = parameters;
    this.linkUrl = linkUrl;
  }

  /** Constructor for NotificationMultiSendRequest without parameters (backward compatibility). */
  public NotificationMultiSendRequest(
      Object source, List<String> receiverIds, String title, String content, String linkUrl) {
    this(source, receiverIds, title, content, null, linkUrl);
  }
}
