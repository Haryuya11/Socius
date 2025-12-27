package com.uit.sociusmvcapp.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Payload dto representing the content of a notification. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PayloadDto {
  private String title;
  private String content;
  private String linkUrl;
}
