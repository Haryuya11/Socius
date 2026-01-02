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
import com.uit.sociusmvcapp.shared.constants.CommonConstant;
import com.uit.sociusmvcapp.shared.response.CursorResponse;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
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
   * Retrieve notifications for the current user.
   *
   * @param cursor the pagination cursor
   * @param limit the number of items to retrieve
   * @return the list of notifications
   */
  @Override
  public CursorResponse<NotificationDto> getNotifications(String cursor, int limit) {
    String clientId = userContentProvider.getUserContent().getClientId();
    LocalDateTime lastCreatedAt = null;
    Integer lastId = null;

    // Decode cursor if provided
    if (cursor != null && !cursor.isEmpty()) {
      String decoded = new String(Base64.getDecoder().decode(cursor));
      String[] parts = decoded.split(CommonConstant.UNDERSCORE);
      lastCreatedAt = LocalDateTime.parse(parts[CommonConstant.INIT_INDEX]);
      lastId = Integer.parseInt(parts[CommonConstant.ONE]);
    }

    // Fetch notifications from repository
    List<NotificationDto> notificationDtos =
        notificationRepository.getNotificationsByClientId(clientId, lastCreatedAt, lastId, limit);

    // Prepare next cursor
    String nextCursor = null;
    if (!notificationDtos.isEmpty()) {
      NotificationDto lastNotification =
          notificationDtos.get(notificationDtos.size() - CommonConstant.ONE);
      String cursorString =
          lastNotification.getCreatedAt().toString()
              + CommonConstant.UNDERSCORE
              + lastNotification.getId();
      nextCursor = Base64.getEncoder().encodeToString(cursorString.getBytes());
    }

    boolean hasNext = notificationDtos.size() >= limit;
    return CursorResponse.<NotificationDto>builder()
        .data(notificationDtos)
        .nextCursor(nextCursor)
        .hasNext(hasNext)
        .build();
  }

  /**
   * Mark a notification as read.
   *
   * @param notificationId the ID of the notification to be marked as read
   */
  @Override
  public void markAsRead(String notificationId) {
    notificationRepository.markAsRead(notificationId);
  }

  /** Mark all notifications as read for the current user. */
  @Override
  public void markAllAsRead() {
    String clientId = userContentProvider.getUserContent().getClientId();
    notificationRepository.markAllAsRead(clientId);
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
