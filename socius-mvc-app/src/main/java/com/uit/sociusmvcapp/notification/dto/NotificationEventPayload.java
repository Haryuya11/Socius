package com.uit.sociusmvcapp.notification.dto;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Payload for notification-related realtime events. Note: targetUserIds (receivers) are now in the
 * RealtimeEvent wrapper, not in this payload.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationEventPayload {

  /** The notification ID. */
  private String notificationId;

  /** The notification title. */
  private String title;

  /** The notification content. */
  private String content;

  /** Additional parameters for the notification. */
  private Map<String, String> parameters;

  /** The redirect URL for the notification. */
  private String redirectUrl;

  /**
   * Creates a NotificationEventPayload from a NotificationDto.
   *
   * @param notification the notification DTO
   * @return the event payload
   */
  public static NotificationEventPayload fromNotificationDto(NotificationDto notification) {
    PayloadDto payload = notification.getPayload();
    return NotificationEventPayload.builder()
        .notificationId(notification.getId() != null ? notification.getId().toString() : null)
        .title(payload != null ? payload.getTitle() : null)
        .content(payload != null ? payload.getContent() : null)
        .parameters(payload != null ? payload.getParameters() : null)
        .redirectUrl(payload != null ? payload.getLinkUrl() : null)
        .build();
  }
}
