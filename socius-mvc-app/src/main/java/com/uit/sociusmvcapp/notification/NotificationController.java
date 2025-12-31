package com.uit.sociusmvcapp.notification;

import com.uit.sociusmvcapp.shared.constants.MessageConstant;
import com.uit.sociusmvcapp.shared.response.Response;
import com.uit.sociusmvcapp.shared.service.I18nService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
  public ResponseEntity<Response> countUnreadNotifications() {
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
}
