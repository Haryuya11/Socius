package com.uit.sociusmvcapp.notification.dto.request;

import com.uit.sociusmvcapp.notification.dto.PayloadDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Request object for creating a new notification. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationCreateRequest {
  private String receiverId;
  private PayloadDto payload;
}
