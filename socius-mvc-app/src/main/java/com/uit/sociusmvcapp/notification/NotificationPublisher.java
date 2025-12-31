package com.uit.sociusmvcapp.notification;

import com.uit.sociusmvcapp.notification.dto.NotificationDto;

/** Service interface for publishing notifications. */
public interface NotificationPublisher {
  /**
   * Publishes a notification.
   *
   * @param notification the notification to be published
   */
  void publishNotification(NotificationDto notification);
}
