package com.uit.sociusmvcapp.message;

import com.uit.sociusmvcapp.message.dto.ConversationDto;
import com.uit.sociusmvcapp.message.dto.ConversationParticipantDto;
import com.uit.sociusmvcapp.message.dto.request.AddParticipantRequest;
import com.uit.sociusmvcapp.message.dto.request.CreateConversationRequest;
import com.uit.sociusmvcapp.message.dto.request.UpdateConversationRequest;
import com.uit.sociusmvcapp.message.dto.request.UpdateParticipantSettingsRequest;
import com.uit.sociusmvcapp.shared.constants.MessageConstant;
import com.uit.sociusmvcapp.shared.response.PageResponse;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** ConversationController handles HTTP requests related to conversations. */
@RestController
@RequiredArgsConstructor
@RequestMapping("/conversations")
public class ConversationController {

  private final I18nService i18nService;
  private final ConversationService conversationService;

  /**
   * Create a new conversation.
   *
   * @param request the conversation creation request
   * @return ResponseEntity containing the created conversation
   */
  @PostMapping
  public ResponseEntity<Response> create(@Valid @RequestBody CreateConversationRequest request) {
    ConversationDto conversation = conversationService.create(request);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.CREATED.value())
            .code(MessageConstant.S_MSG_001)
            .message(i18nService.getMessage(MessageConstant.S_MSG_001))
            .data(conversation)
            .build();
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  /**
   * Get a conversation by its ID.
   *
   * @param conversationId the conversation ID
   * @return ResponseEntity containing the conversation
   */
  @GetMapping("/{conversationId}")
  public ResponseEntity<Response> getByConversationId(@PathVariable String conversationId) {
    ConversationDto conversation = conversationService.getByConversationId(conversationId);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_MSG_002)
            .message(i18nService.getMessage(MessageConstant.S_MSG_002))
            .data(conversation)
            .build();
    return ResponseEntity.ok(response);
  }

  /**
   * Update a conversation.
   *
   * @param conversationId the conversation ID
   * @param request the update request
   * @return ResponseEntity containing the updated conversation
   */
  @PutMapping("/{conversationId}")
  public ResponseEntity<Response> update(
      @PathVariable String conversationId, @Valid @RequestBody UpdateConversationRequest request) {
    ConversationDto conversation = conversationService.update(conversationId, request);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_MSG_003)
            .message(i18nService.getMessage(MessageConstant.S_MSG_003))
            .data(conversation)
            .build();
    return ResponseEntity.ok(response);
  }

  /**
   * Delete a conversation.
   *
   * @param conversationId the conversation ID
   * @return ResponseEntity indicating success
   */
  @DeleteMapping("/{conversationId}")
  public ResponseEntity<Response> delete(@PathVariable String conversationId) {
    conversationService.delete(conversationId);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_MSG_004)
            .message(i18nService.getMessage(MessageConstant.S_MSG_004))
            .build();
    return ResponseEntity.ok(response);
  }

  /**
   * Get all conversations for the current user.
   *
   * @param pageNumber the page number
   * @param pageSize the page size
   * @return ResponseEntity containing paginated conversations
   */
  @GetMapping
  public ResponseEntity<Response> getConversations(
      @RequestParam(defaultValue = "1") int pageNumber,
      @RequestParam(defaultValue = "20") int pageSize) {
    PageResponse<ConversationDto> conversations =
        conversationService.getConversations(pageNumber, pageSize);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_MSG_005)
            .message(i18nService.getMessage(MessageConstant.S_MSG_005))
            .data(conversations)
            .build();
    return ResponseEntity.ok(response);
  }

  /**
   * Get all participants of a conversation.
   *
   * @param conversationId the conversation ID
   * @return ResponseEntity containing the participants
   */
  @GetMapping("/{conversationId}/participants")
  public ResponseEntity<Response> getParticipants(@PathVariable String conversationId) {
    List<ConversationParticipantDto> participants =
        conversationService.getParticipants(conversationId);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_MSG_006)
            .message(i18nService.getMessage(MessageConstant.S_MSG_006))
            .data(participants)
            .build();
    return ResponseEntity.ok(response);
  }

  /**
   * Add a participant to a conversation.
   *
   * @param conversationId the conversation ID
   * @param request the add participant request
   * @return ResponseEntity containing the added participant
   */
  @PostMapping("/{conversationId}/participants")
  public ResponseEntity<Response> addParticipant(
      @PathVariable String conversationId, @Valid @RequestBody AddParticipantRequest request) {
    ConversationParticipantDto participant =
        conversationService.addParticipant(conversationId, request);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.CREATED.value())
            .code(MessageConstant.S_MSG_007)
            .message(i18nService.getMessage(MessageConstant.S_MSG_007))
            .data(participant)
            .build();
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  /**
   * Remove a participant from a conversation.
   *
   * @param conversationId the conversation ID
   * @param employeeId the employee ID to remove
   * @return ResponseEntity indicating success
   */
  @DeleteMapping("/{conversationId}/participants/{employeeId}")
  public ResponseEntity<Response> removeParticipant(
      @PathVariable String conversationId, @PathVariable String employeeId) {
    conversationService.removeParticipant(conversationId, employeeId);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_MSG_008)
            .message(i18nService.getMessage(MessageConstant.S_MSG_008))
            .build();
    return ResponseEntity.ok(response);
  }

  /**
   * Leave a conversation (current user).
   *
   * @param conversationId the conversation ID
   * @return ResponseEntity indicating success
   */
  @PostMapping("/{conversationId}/leave")
  public ResponseEntity<Response> leaveConversation(@PathVariable String conversationId) {
    conversationService.leaveConversation(conversationId);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_MSG_009)
            .message(i18nService.getMessage(MessageConstant.S_MSG_009))
            .build();
    return ResponseEntity.ok(response);
  }

  /**
   * Update participant settings (mute, pin).
   *
   * @param conversationId the conversation ID
   * @param request the settings update request
   * @return ResponseEntity indicating success
   */
  @PutMapping("/{conversationId}/settings")
  public ResponseEntity<Response> updateSettings(
      @PathVariable String conversationId,
      @Valid @RequestBody UpdateParticipantSettingsRequest request) {
    conversationService.updateParticipantSettings(conversationId, request);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_MSG_010)
            .message(i18nService.getMessage(MessageConstant.S_MSG_010))
            .build();
    return ResponseEntity.ok(response);
  }

  /**
   * Mark messages as read in a conversation.
   *
   * @param conversationId the conversation ID
   * @param messageId the last read message ID
   * @return ResponseEntity indicating success
   */
  @PutMapping("/{conversationId}/read/{messageId}")
  public ResponseEntity<Response> markAsRead(
      @PathVariable String conversationId, @PathVariable Long messageId) {
    conversationService.markAsRead(conversationId, messageId);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_MSG_011)
            .message(i18nService.getMessage(MessageConstant.S_MSG_011))
            .build();
    return ResponseEntity.ok(response);
  }
}
