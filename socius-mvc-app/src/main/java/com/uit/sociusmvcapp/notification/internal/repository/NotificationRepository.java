package com.uit.sociusmvcapp.notification.internal.repository;

import com.uit.sociusmvcapp.notification.internal.domain.Notification;
import com.uit.sociusmvcapp.notification.internal.persistence.NotificationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/** Repository for Notification entity. */
@Repository
@RequiredArgsConstructor
public class NotificationRepository {
  /** MyBatis Mapper for Notification entity. */
  private final NotificationMapper notificationMapper;

  /**
   * Create a new notification record.
   *
   * @param notification the notification to be created
   */
  public void insert(Notification notification) {
    notificationMapper.insert(notification);
  }

  /**
   * Count unread notifications by client ID.
   *
   * @param clientId the client ID
   * @return the count of unread notifications
   */
  public Integer countUnreadByClientId(String clientId) {
    return notificationMapper.countUnreadByClientId(clientId);
  }
}
