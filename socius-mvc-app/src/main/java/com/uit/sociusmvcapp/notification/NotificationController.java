package com.uit.sociusmvcapp.notification;

import com.uit.sociusmvcapp.notification.dto.NotificationDto;
import com.uit.sociusmvcapp.shared.constants.MessageConstant;
import com.uit.sociusmvcapp.shared.response.CursorResponse;
import com.uit.sociusmvcapp.shared.response.Response;
import com.uit.sociusmvcapp.shared.service.I18nService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** NotificationController handles HTTP requests related to notifications. */
@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {
  /** I18nService for internationalization messages. */
  private final I18nService i18nService;

  /** NotificationService for notification-related operations. */
  private final NotificationService notificationService;

  /**
   * Count unread notifications for the authenticated user.
   *
   * @return ResponseEntity containing the count of unread notifications
   */
  @GetMapping("/unread")
  public ResponseEntity<Response> countUnread() {
    Integer unreadCount = notificationService.countUnread();
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_NOTIFY_001)
            .message(i18nService.getMessage(MessageConstant.S_NOTIFY_001))
            .data(unreadCount)
            .build();
    return ResponseEntity.ok(response);
  }

  /**
   * Retrieve notifications for the authenticated user.
   *
   * @return ResponseEntity containing the list of notifications
   */
  @GetMapping
  public ResponseEntity<Response> getNotifications(
      @RequestParam(required = false) String cursor, @RequestParam(defaultValue = "10") int limit) {
    CursorResponse<NotificationDto> result = notificationService.getNotifications(cursor, limit);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_NOTIFY_002)
            .message(i18nService.getMessage(MessageConstant.S_NOTIFY_002))
            .data(result)
            .build();
    return ResponseEntity.ok(response);
  }

  /**
   * Mark a notification as read.
   *
   * @param notificationId the ID of the notification to be marked as read
   * @return ResponseEntity indicating the operation result
   */
  @PutMapping("/read/{notificationId}")
  public ResponseEntity<Response> readNotification(@PathVariable String notificationId) {
    notificationService.markAsRead(notificationId);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_NOTIFY_003)
            .message(i18nService.getMessage(MessageConstant.S_NOTIFY_003))
            .data(null)
            .build();
    return ResponseEntity.ok(response);
  }

  /**
   * Mark all notifications as read.
   *
   * @return ResponseEntity indicating the operation result
   */
  @PutMapping("/mark-all")
  public ResponseEntity<Response> markAllAsRead() {
    notificationService.markAllAsRead();
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_NOTIFY_004)
            .message(i18nService.getMessage(MessageConstant.S_NOTIFY_004))
            .data(null)
            .build();
    return ResponseEntity.ok(response);
  }
}
