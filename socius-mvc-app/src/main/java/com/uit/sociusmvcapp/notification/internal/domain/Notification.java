package com.uit.sociusmvcapp.notification.internal.domain;

import com.uit.sociusmvcapp.shared.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/** Notification entity representing a notification sent to a user. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Notification extends BaseEntity {
  private String receiverId;
  private Short deliveryType;
  private String payload;
  private Short isRead;
}
