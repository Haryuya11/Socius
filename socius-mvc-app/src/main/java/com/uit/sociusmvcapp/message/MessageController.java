package com.uit.sociusmvcapp.message;

import com.uit.sociusmvcapp.message.dto.MessageDto;
import com.uit.sociusmvcapp.message.dto.MessageReactionDto;
import com.uit.sociusmvcapp.message.dto.request.MessageReactionRequest;
import com.uit.sociusmvcapp.message.dto.request.SendMessageRequest;
import com.uit.sociusmvcapp.message.dto.request.UpdateMessageRequest;
import com.uit.sociusmvcapp.shared.constants.MessageConstant;
import com.uit.sociusmvcapp.shared.response.Response;
import com.uit.sociusmvcapp.shared.service.I18nService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** MessageController handles HTTP requests related to messages. */
@RestController
@RequiredArgsConstructor
@RequestMapping("/messages")
public class MessageController {

  private final I18nService i18nService;
  private final MessageService messageService;

  /**
   * Send a new message.
   *
   * @param request the send message request
   * @return ResponseEntity containing the sent message
   */
  @PostMapping
  public ResponseEntity<Response> sendMessage(@Valid @RequestBody SendMessageRequest request) {
    MessageDto message = messageService.sendMessage(request);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.CREATED.value())
            .code(MessageConstant.S_MSG_012)
            .message(i18nService.getMessage(MessageConstant.S_MSG_012))
            .data(message)
            .build();
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  /**
   * Get a message by its ID.
   *
   * @param messageId the message ID
   * @return ResponseEntity containing the message
   */
  @GetMapping("/{messageId}")
  public ResponseEntity<Response> getByMessageId(@PathVariable String messageId) {
    MessageDto message = messageService.getByMessageId(messageId);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_MSG_013)
            .message(i18nService.getMessage(MessageConstant.S_MSG_013))
            .data(message)
            .build();
    return ResponseEntity.ok(response);
  }

  /**
   * Update a message.
   *
   * @param messageId the message ID
   * @param request the update request
   * @return ResponseEntity containing the updated message
   */
  @PutMapping("/{messageId}")
  public ResponseEntity<Response> updateMessage(
      @PathVariable String messageId, @Valid @RequestBody UpdateMessageRequest request) {
    MessageDto message = messageService.updateMessage(messageId, request);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_MSG_015)
            .message(i18nService.getMessage(MessageConstant.S_MSG_015))
            .data(message)
            .build();
    return ResponseEntity.ok(response);
  }

  /**
   * Delete a message.
   *
   * @param messageId the message ID
   * @return ResponseEntity indicating success
   */
  @DeleteMapping("/{messageId}")
  public ResponseEntity<Response> deleteMessage(@PathVariable String messageId) {
    messageService.deleteMessage(messageId);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_MSG_016)
            .message(i18nService.getMessage(MessageConstant.S_MSG_016))
            .build();
    return ResponseEntity.ok(response);
  }

  /**
   * Add a reaction to a message.
   *
   * @param request the reaction request
   * @return ResponseEntity containing the added reaction
   */
  @PostMapping("/reactions")
  public ResponseEntity<Response> addReaction(@Valid @RequestBody MessageReactionRequest request) {
    MessageReactionDto reaction = messageService.addReaction(request);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.CREATED.value())
            .code(MessageConstant.S_MSG_017)
            .message(i18nService.getMessage(MessageConstant.S_MSG_017))
            .data(reaction)
            .build();
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  /**
   * Remove a reaction from a message.
   *
   * @param request the reaction request
   * @return ResponseEntity indicating success
   */
  @DeleteMapping("/reactions")
  public ResponseEntity<Response> removeReaction(
      @Valid @RequestBody MessageReactionRequest request) {
    messageService.removeReaction(request);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_MSG_018)
            .message(i18nService.getMessage(MessageConstant.S_MSG_018))
            .build();
    return ResponseEntity.ok(response);
  }

  /**
   * Get all reactions for a message.
   *
   * @param messageId the message ID
   * @return ResponseEntity containing the reactions
   */
  @GetMapping("/{messageId}/reactions")
  public ResponseEntity<Response> getReactions(@PathVariable String messageId) {
    List<MessageReactionDto> reactions = messageService.getReactions(messageId);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_MSG_019)
            .message(i18nService.getMessage(MessageConstant.S_MSG_019))
            .data(reactions)
            .build();
    return ResponseEntity.ok(response);
  }
}
