package com.uit.sociusmvcapp.notification;

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
}
