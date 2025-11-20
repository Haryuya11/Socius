package com.uit.sociuscoremodules.notification.service.impl;

import com.uit.sociuscoremodules.notification.converter.NotificationConverter;
import com.uit.sociuscoremodules.notification.repository.NotificationRepository;
import com.uit.sociuscoremodules.notification.service.NotificationService;
import com.uit.sociuscoremodules.shared.security.UserContentProvider;
import com.uit.sociuscoremodules.shared.service.impl.BaseServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** Implementation of NotificationService for Notification-related operations. */
@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl extends BaseServiceImpl implements NotificationService {
  /** Repository for accessing notification data. */
  private final NotificationRepository notificationRepository;

  /** Converter for transforming notification data. */
  private final NotificationConverter notificationConverter;

  /** Provider for user content. */
  private final UserContentProvider userContentProvider;

  /**
   * Count unread notifications for the current user.
   *
   * @return the count of unread notifications
   */
  @Override
  public Integer countUnread() {
    String clientId = userContentProvider.getUserContent().getClientId();
    return notificationRepository.countUnreadByClientId(clientId);
  }
}
