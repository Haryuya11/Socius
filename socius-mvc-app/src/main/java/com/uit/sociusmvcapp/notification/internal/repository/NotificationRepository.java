package com.uit.sociusmvcapp.notification.internal.repository;

import com.uit.sociusmvcapp.notification.dto.NotificationDto;
import com.uit.sociusmvcapp.notification.internal.converter.NotificationConverter;
import com.uit.sociusmvcapp.notification.internal.domain.Notification;
import com.uit.sociusmvcapp.notification.internal.persistence.NotificationMapper;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/** Repository for Notification entity. */
@Repository
@RequiredArgsConstructor
public class NotificationRepository {
  /** MyBatis Mapper for Notification entity. */
  private final NotificationMapper notificationMapper;

  /** Converter for transforming notification data. */
  private final NotificationConverter notificationConverter;

  /**
   * Create a new notification record.
   *
   * @param notification the notification to be created
   */
  public void insert(Notification notification) {
    notificationMapper.insert(notification);
  }

  /**
   * Batch insert notification records.
   *
   * @param notifications the list of notifications to be created
   */
  public void batchInsert(List<Notification> notifications) {
    if (notifications != null && !notifications.isEmpty()) {
      notificationMapper.batchInsert(notifications);
    }
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

  /**
   * Retrieve notifications by client ID with cursor pagination. * @param clientId the client ID
   * (receiver)
   *
   * @param lastCreatedAt the 'created_at' of the last item in the previous list (can be null for
   *     1st page)
   * @param lastId the 'id' of the last item in the previous list (can be null for 1st page)
   * @param limit the number of items to retrieve
   * @return the list of notifications
   */
  public List<NotificationDto> getNotificationsByClientId(
      String clientId, LocalDateTime lastCreatedAt, Integer lastId, int limit) {
    return notificationConverter.entitiesToDtos(
        notificationMapper.getNotificationsByClientId(clientId, lastCreatedAt, lastId, limit));
  }

  /**
   * Mark a notification as read.
   *
   * @param notificationId the ID of the notification to be marked as read
   * @param clientId the client ID
   */
  public void markAsRead(Long notificationId, String clientId) {
    notificationMapper.markAsRead(notificationId, clientId);
  }

  /**
   * Mark all notifications as read for the given client ID.
   *
   * @param clientId the client ID
   */
  public void markAllAsRead(String clientId) {
    notificationMapper.markAllAsRead(clientId);
  }
}
