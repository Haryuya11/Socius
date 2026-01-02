package com.uit.sociusmvcapp.notification;

import com.uit.sociusmvcapp.notification.dto.NotificationDto;
import com.uit.sociusmvcapp.shared.response.CursorResponse;

/** Service interface for Notification-related operations. */
public interface NotificationService {
  /**
   * Count unread notifications for the current user.
   *
   * @return the count of unread notifications
   */
  Integer countUnread();

  /**
   * Send a notification to a user.
   *
   * @param receiverId the ID of the notification receiver
   * @param title the title of the notification
   * @param content the content of the notification
   * @param linkUrl the link URL of the notification
   */
  void sendNotification(String receiverId, String title, String content, String linkUrl);

  /**
   * Retrieve notifications for the current user.
   *
   * @param cursor the pagination cursor
   * @param limit the number of items to retrieve
   * @return the list of notifications
   */
  CursorResponse<NotificationDto> getNotifications(String cursor, int limit);

  /**
   * Mark a notification as read.
   *
   * @param notificationId the ID of the notification to be marked as read
   */
  void markAsRead(Long notificationId);

  /** Mark all notifications as read for the current user. */
  void markAllAsRead();
}
