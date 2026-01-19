package com.uit.sociusmvcapp.message;

import com.uit.sociusmvcapp.azure.blob.UploadFileDto;
import com.uit.sociusmvcapp.message.dto.ConversationDto;
import com.uit.sociusmvcapp.message.dto.ConversationParticipantDto;
import com.uit.sociusmvcapp.message.dto.FileMetadataDto;
import com.uit.sociusmvcapp.message.dto.MessageDto;
import com.uit.sociusmvcapp.message.dto.request.AddParticipantsRequest;
import com.uit.sociusmvcapp.message.dto.request.CreateGroupConversationRequest;
import com.uit.sociusmvcapp.message.dto.request.UpdateConversationRequest;
import com.uit.sociusmvcapp.message.dto.request.UpdateParticipantSettingsRequest;
import com.uit.sociusmvcapp.shared.constants.MessageConstant;
import com.uit.sociusmvcapp.shared.response.CursorResponse;
import com.uit.sociusmvcapp.shared.response.Response;
import com.uit.sociusmvcapp.shared.service.I18nService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import org.springframework.web.multipart.MultipartFile;

/** ConversationController handles HTTP requests related to conversations. */
@RestController
@RequiredArgsConstructor
@RequestMapping("/conversations")
public class ConversationController {

  private final I18nService i18nService;
  private final ConversationService conversationService;
  private final MessageService messageService;

  /**
   * Get or create a direct conversation with another user. Implements lazy creation - returns
   * existing conversation if it exists, creates new one otherwise.
   *
   * @param targetEmployeeId the target employee ID
   * @return ResponseEntity containing the conversation (existing or newly created)
   */
  @PostMapping("/direct/{targetEmployeeId}")
  public ResponseEntity<Response> getOrCreateDirectConversation(
      @PathVariable String targetEmployeeId) {

    ConversationDto conversation =
        conversationService.getOrCreateDirectConversation(targetEmployeeId);

    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_MSG_001)
            .message(i18nService.getMessage(MessageConstant.S_MSG_001))
            .data(conversation)
            .build();

    return ResponseEntity.ok(response);
  }

  /**
   * Create a new group conversation explicitly.
   *
   * @param request the group conversation creation request
   * @return ResponseEntity containing the created group conversation
   */
  @PostMapping("/group")
  public ResponseEntity<Response> createGroupConversation(
      @Valid @RequestBody CreateGroupConversationRequest request) {
    ConversationDto conversation = conversationService.createGroupConversation(request);
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
   * Get all conversations for the current user with cursor-based pagination.
   *
   * @param cursor the cursor for pagination (optional)
   * @param limit the number of items to retrieve
   * @return ResponseEntity containing cursor-paginated conversations
   */
  @GetMapping
  public ResponseEntity<Response> getConversations(
      @RequestParam(required = false) String cursor, @RequestParam(defaultValue = "20") int limit) {
    CursorResponse<ConversationDto> conversations =
        conversationService.getConversations(cursor, limit);
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
      @PathVariable String conversationId, @Valid @RequestBody AddParticipantsRequest request) {

    List<ConversationParticipantDto> participant =
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
   * @param employeeIds the list of employee IDs to remove
   * @return ResponseEntity indicating success
   */
  @DeleteMapping("/{conversationId}/participants")
  public ResponseEntity<Response> removeParticipant(
      @PathVariable String conversationId, @RequestBody List<String> employeeIds) {
    conversationService.removeParticipant(conversationId, employeeIds);
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
      @PathVariable String conversationId, @PathVariable String messageId) {
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

  /**
   * Get messages in a conversation.
   *
   * @param conversationId the conversation ID
   * @param cursor the pagination cursor
   * @param limit the number of items to retrieve
   * @return ResponseEntity containing the messages
   */
  @GetMapping("/{conversationId}/messages")
  public ResponseEntity<Response> getMessages(
      @PathVariable String conversationId,
      @RequestParam(required = false) String cursor,
      @RequestParam(defaultValue = "20") int limit) {
    CursorResponse<MessageDto> messages = messageService.getMessages(conversationId, cursor, limit);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_MSG_014)
            .message(i18nService.getMessage(MessageConstant.S_MSG_014))
            .data(messages)
            .build();
    return ResponseEntity.ok(response);
  }

  /**
   * Upload a file avatar to a conversation.
   *
   * @param conversationId the conversation ID
   * @param file the file to upload
   * @return ResponseEntity containing the uploaded file metadata
   */
  @PostMapping("/{conversationId}/avatar")
  public ResponseEntity<Response> uploadFile(
      @PathVariable String conversationId, @RequestParam("file") MultipartFile file) {
    UploadFileDto metadata = conversationService.uploadFile(conversationId, file);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_MSG_014)
            .message(i18nService.getMessage(MessageConstant.S_MSG_020))
            .data(metadata)
            .build();
    return ResponseEntity.ok(response);
  }

  /**
   * Upload files for a message in a conversation.
   *
   * @param conversationId the conversation ID
   * @param files the files to upload
   * @return ResponseEntity containing the list of file metadata
   */
  @PostMapping("/{conversationId}/files")
  public ResponseEntity<Response> uploadMessageFiles(
      @PathVariable String conversationId, @RequestParam("files") List<MultipartFile> files) {
    List<FileMetadataDto> metadataList = messageService.uploadMessageFiles(conversationId, files);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.CREATED.value())
            .code(MessageConstant.S_MSG_021)
            .message(i18nService.getMessage(MessageConstant.S_MSG_021))
            .data(metadataList)
            .build();
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  /**
   * Download a single file from a conversation.
   *
   * @param conversationId the conversation ID
   * @param response the HTTP servlet response
   * @param filePath the file path to download (URL encoded)
   * @throws IOException if download fails
   */
  @GetMapping("/{conversationId}/files/download")
  public void downloadFile(
      @PathVariable String conversationId,
      @RequestParam("filePath") String filePath,
      HttpServletResponse response)
      throws IOException {
    String fileName = messageService.getOriginalFileName(filePath);
    String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);

    response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
    response.setHeader(
        HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFileName);

    messageService.downloadFile(conversationId, filePath, response.getOutputStream());
    response.flushBuffer();
  }

  /**
   * Download multiple files as a ZIP archive from a conversation.
   *
   * @param conversationId the conversation ID
   * @param response the HTTP servlet response
   * @param filePaths the list of file paths to download
   * @throws IOException if download fails
   */
  @PostMapping("/{conversationId}/files/download-zip")
  public void downloadFilesAsZip(
      @PathVariable String conversationId,
      @RequestBody List<String> filePaths,
      HttpServletResponse response)
      throws IOException {
    String zipFileName = UUID.randomUUID().toString() + ".zip";

    response.setContentType("application/zip");
    response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + zipFileName);

    messageService.downloadFilesAsZip(conversationId, filePaths, response.getOutputStream());
    response.flushBuffer();
  }
}
