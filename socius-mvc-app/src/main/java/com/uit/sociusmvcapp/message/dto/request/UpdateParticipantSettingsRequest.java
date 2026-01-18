package com.uit.sociusmvcapp.message.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Request object for updating participant settings. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateParticipantSettingsRequest {
  private Boolean isMuted;
  private Boolean isPinned;
}
