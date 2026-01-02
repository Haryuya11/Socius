package com.uit.sociusmvcapp.notification.internal.persistence;

import com.uit.sociusmvcapp.notification.internal.domain.Notification;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/** MyBatis Mapper interface for Notification entity. */
@Mapper
public interface NotificationMapper {

  /**
   * Create a new notification record.
   *
   * @param notification the notification to be created
   */
  void insert(Notification notification);

  /**
   * Count unread notifications by client ID.
   *
   * @param clientId the client ID
   * @return the count of unread notifications
   */
  Integer countUnreadByClientId(String clientId);

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
  List<Notification> getNotificationsByClientId(
      @Param("clientId") String clientId,
      @Param("lastCreatedAt") LocalDateTime lastCreatedAt,
      @Param("lastId") Integer lastId,
      @Param("limit") int limit);

  /**
   * Mark a notification as read.
   *
   * @param notificationId the ID of the notification to be marked as read
   */
  void markAsRead(String notificationId);

  /**
   * Mark all notifications as read for the given client ID.
   *
   * @param clientId the client ID
   */
  void markAllAsRead(String clientId);
}
