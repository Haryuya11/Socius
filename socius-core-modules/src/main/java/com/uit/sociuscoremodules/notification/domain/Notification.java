package com.uit.sociuscoremodules.notification.domain;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/** Notification entity representing a notification sent to a user. */
@Getter
@Setter
public class Notification {
  private Long id;
  private String receiverId;
  private Short deliveryType;
  private String payload;
  private Short isRead;
  private LocalDateTime createdAt;
}
