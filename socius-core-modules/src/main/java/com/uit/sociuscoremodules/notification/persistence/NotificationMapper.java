package com.uit.sociuscoremodules.notification.persistence;

import com.uit.sociuscoremodules.notification.domain.Notification;
import org.apache.ibatis.annotations.Mapper;

/** MyBatis Mapper interface for Employee entity. */
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
}
