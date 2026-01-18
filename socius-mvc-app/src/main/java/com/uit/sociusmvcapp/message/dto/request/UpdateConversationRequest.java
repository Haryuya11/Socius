package com.uit.sociusmvcapp.message.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Request object for updating a conversation. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateConversationRequest {
  private String name;
  private String avatarUrl;
}
