package com.uit.sociusmvcapp.message.dto.request;

import com.uit.sociusmvcapp.message.dto.FileMetadataDto;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Request object for sending a message. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SendMessageRequest {

  @NotBlank(message = "Conversation ID is required")
  private String conversationId;

  private String content;

  @NotBlank(message = "Message type is required")
  private String messageType;

  private String parentMessageId;
  private List<FileMetadataDto> metadata;
}
