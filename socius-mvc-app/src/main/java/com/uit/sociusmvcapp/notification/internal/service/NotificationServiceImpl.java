package com.uit.sociusmvcapp.notification.internal.service;

import com.uit.sociusmvcapp.iam.UserContentProvider;
import com.uit.sociusmvcapp.notification.NotificationPublisher;
import com.uit.sociusmvcapp.notification.NotificationService;
import com.uit.sociusmvcapp.notification.dto.NotificationDto;
import com.uit.sociusmvcapp.notification.dto.PayloadDto;
import com.uit.sociusmvcapp.notification.dto.request.NotificationCreateRequest;
import com.uit.sociusmvcapp.notification.internal.converter.NotificationConverter;
import com.uit.sociusmvcapp.notification.internal.domain.Notification;
import com.uit.sociusmvcapp.notification.internal.repository.NotificationRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** Implementation of NotificationService for Notification-related operations. */
@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
  /** Repository for accessing notification data. */
  private final NotificationRepository notificationRepository;

  /** Converter for transforming notification data. */
  private final NotificationConverter notificationConverter;

  /** Provider for user content. */
  private final UserContentProvider userContentProvider;

  /** Publisher for sending notifications. */
  private final NotificationPublisher notificationPublisher;

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

  /**
   * Send a notification to a user.
   *
   * @param receiverId the ID of the notification receiver
   * @param title the title of the notification
   * @param content the content of the notification
   * @param linkUrl the link URL of the notification
   */
  @Override
  public void sendNotification(String receiverId, String title, String content, String linkUrl) {
    LocalDateTime dateTime = LocalDateTime.now();
    PayloadDto payloadDto = buildPayload(title, content, linkUrl);
    NotificationCreateRequest request = buildNotificationRequest(receiverId, payloadDto);
    Notification notification = notificationConverter.fromCreateRequest(request, dateTime);
    notificationRepository.insert(notification);
    NotificationDto dto = notificationConverter.entityToDto(notification);
    notificationPublisher.publishNotification(dto);
  }

  /**
   * Build a payload object.
   *
   * @param title the title of the payload
   * @param content the content of the payload
   * @param linkUrl the link URL of the payload
   * @return the constructed Payload object
   */
  private PayloadDto buildPayload(String title, String content, String linkUrl) {
    return PayloadDto.builder().title(title).content(content).linkUrl(linkUrl).build();
  }

  /**
   * Build a notification object.
   *
   * @param receiverId the ID of the notification receiver
   * @param payloadDto the payload of the notification
   * @return the constructed Notification object
   */
  private NotificationCreateRequest buildNotificationRequest(
      String receiverId, PayloadDto payloadDto) {
    return NotificationCreateRequest.builder().receiverId(receiverId).payload(payloadDto).build();
  }
}
