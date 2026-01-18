package com.uit.sociusmvcapp.message.dto.request;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Request object for adding a participant to a conversation. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddParticipantsRequest {

  @NotEmpty(message = "Participants are required")
  private List<ParticipantRequest> participants;
}
