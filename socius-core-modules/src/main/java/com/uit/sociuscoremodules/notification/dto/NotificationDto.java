package com.uit.sociuscoremodules.notification.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Data Transfer Object for Notification entity. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationDto {
  private Long id;
  private String receiverId;
  private Short deliveryType;
  private PayloadDto payload;
  private Short isRead;
  private LocalDateTime createdAt;
}
