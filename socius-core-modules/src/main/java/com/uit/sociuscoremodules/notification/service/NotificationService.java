package com.uit.sociuscoremodules.notification.service;

/** Service interface for Notification-related operations. */
public interface NotificationService {
  /**
   * Count unread notifications for the current user.
   *
   * @return the count of unread notifications
   */
  Integer countUnread();
}
