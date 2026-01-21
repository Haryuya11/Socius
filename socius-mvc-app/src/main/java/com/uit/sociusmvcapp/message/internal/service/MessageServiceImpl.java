package com.uit.sociusmvcapp.message.internal.service;

import com.uit.sociusmvcapp.iam.UserContentProvider;
import com.uit.sociusmvcapp.message.MessagePublisher;
import com.uit.sociusmvcapp.message.MessageService;
import com.uit.sociusmvcapp.message.dto.FileDownloadInfoDto;
import com.uit.sociusmvcapp.message.dto.FileMetadataDto;
import com.uit.sociusmvcapp.message.dto.MessageDto;
import com.uit.sociusmvcapp.message.dto.MessageReactionDto;
import com.uit.sociusmvcapp.message.dto.request.FileDownloadRequest;
import com.uit.sociusmvcapp.message.dto.request.MessageReactionRequest;
import com.uit.sociusmvcapp.message.dto.request.SendMessageRequest;
import com.uit.sociusmvcapp.message.dto.request.UpdateMessageRequest;
import com.uit.sociusmvcapp.message.internal.adapter.MessageBlobAdapter;
import com.uit.sociusmvcapp.message.internal.domain.MessageReaction;
import com.uit.sociusmvcapp.message.internal.repository.ConversationParticipantRepository;
import com.uit.sociusmvcapp.message.internal.repository.ConversationRepository;
import com.uit.sociusmvcapp.message.internal.repository.MessageReactionRepository;
import com.uit.sociusmvcapp.message.internal.repository.MessageRepository;
import com.uit.sociusmvcapp.shared.constants.CommonConstant;
import com.uit.sociusmvcapp.shared.constants.MessageConstant;
import com.uit.sociusmvcapp.shared.response.CursorResponse;
import com.uit.sociusmvcapp.shared.service.ExceptionFactory;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/** Implementation of MessageService for Message-related operations. */
@Service
@Slf4j
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

  private final MessageRepository messageRepository;
  private final MessageReactionRepository reactionRepository;
  private final ConversationRepository conversationRepository;
  private final ConversationParticipantRepository participantRepository;
  private final MessagePublisher messagePublisher;
  private final UserContentProvider userContentProvider;
  private final MessageBlobAdapter messageBlobAdapter;

  /**
   * Sends a message in a conversation.
   *
   * @param request the send message request
   * @return the sent message DTO
   */
  @Override
  @Transactional
  public MessageDto sendMessage(SendMessageRequest request) {
    String currentClientId = getCurrentClientId();
    validateParticipant(request.getConversationId(), currentClientId);

    MessageDto messageDto = messageRepository.createMessage(request, currentClientId);
    conversationRepository.updateLastMessage(
        request.getConversationId(), messageDto.getMessageId());
    List<String> targetUserIds =
        participantRepository.findEmployeeIdsByConversationId(request.getConversationId());
    messagePublisher.publishNewMessage(messageDto, targetUserIds);
    return messageDto;
  }

  /**
   * Retrieves a message by its ID.
   *
   * @param messageId the message ID
   * @return the message DTO
   */
  @Override
  public MessageDto getByMessageId(String messageId) {

    MessageDto message = messageRepository.findByMessageId(messageId);
    if (message == null) {
      throw ExceptionFactory.notFound(MessageConstant.E_MSG_008);
    }

    // Verify user is a participant
    validateParticipant(message.getConversationId(), getCurrentClientId());

    // Regenerate SAS tokens for file metadata and populate reactions
    refreshFileUrls(message);
    populateReactions(message);

    return message;
  }

  /**
   * Retrieves messages from a conversation with cursor-based pagination.
   *
   * @param conversationId the conversation ID
   * @param cursor the pagination cursor
   * @param limit the maximum number of messages to retrieve
   * @return a cursor response containing the messages
   */
  @Override
  public CursorResponse<MessageDto> getMessages(String conversationId, String cursor, int limit) {

    validateParticipant(conversationId, getCurrentClientId());

    LocalDateTime lastCreatedAt = null;
    Long lastId = null;

    if (cursor != null && !cursor.isEmpty()) {
      String decoded = new String(Base64.getDecoder().decode(cursor));
      String[] parts = decoded.split(CommonConstant.UNDERSCORE);
      lastCreatedAt = LocalDateTime.parse(parts[CommonConstant.INIT_INDEX]);
      lastId = Long.parseLong(parts[CommonConstant.ONE]);
    }

    List<MessageDto> messages =
        messageRepository.findByConversationId(conversationId, lastCreatedAt, lastId, limit);

    // Regenerate SAS tokens for file metadata and populate reactions
    messages.forEach(
        message -> {
          refreshFileUrls(message);
          populateReactions(message);
        });

    String nextCursor = null;
    if (!messages.isEmpty()) {
      MessageDto lastMessage = messages.get(messages.size() - CommonConstant.ONE);
      String cursorString =
          lastMessage.getCreatedAt().toString() + CommonConstant.UNDERSCORE + lastMessage.getId();
      nextCursor = Base64.getEncoder().encodeToString(cursorString.getBytes());
    }

    boolean hasNext = messages.size() == limit;
    return CursorResponse.<MessageDto>builder()
        .data(messages)
        .nextCursor(nextCursor)
        .hasNext(hasNext)
        .build();
  }

  /**
   * Updates a message's content.
   *
   * @param messageId the message ID
   * @param request the update message request
   * @return the updated message
   */
  @Override
  @Transactional
  public MessageDto updateMessage(String messageId, UpdateMessageRequest request) {

    MessageDto message = messageRepository.findByMessageId(messageId);
    if (message == null) {
      throw ExceptionFactory.notFound(MessageConstant.E_MSG_008);
    }

    if (!message.getSenderId().equals(getCurrentClientId())) {
      throw ExceptionFactory.badRequest(MessageConstant.E_MSG_009);
    }

    messageRepository.updateContent(messageId, request.getContent());

    MessageDto updatedMessage = messageRepository.findByMessageId(messageId);

    List<String> targetUserIds =
        participantRepository.findEmployeeIdsByConversationId(message.getConversationId());

    messagePublisher.publishMessageUpdated(updatedMessage, targetUserIds);
    return updatedMessage;
  }

  /**
   * Deletes a message.
   *
   * @param messageId the message ID
   */
  @Override
  @Transactional
  public void deleteMessage(String messageId) {
    MessageDto message = messageRepository.findByMessageId(messageId);
    if (message == null) {
      throw ExceptionFactory.notFound(MessageConstant.E_MSG_008);
    }

    if (!message.getSenderId().equals(getCurrentClientId())) {
      throw ExceptionFactory.badRequest(MessageConstant.E_MSG_009);
    }

    String conversationId = message.getConversationId();
    messageRepository.softDelete(messageId);

    List<String> targetUserIds =
        participantRepository.findEmployeeIdsByConversationId(conversationId);

    messagePublisher.publishMessageDeleted(conversationId, messageId, targetUserIds);
  }

  /**
   * Adds a reaction to a message.
   *
   * @param request the reaction request
   * @return the added message reaction
   */
  @Override
  @Transactional
  public MessageReactionDto addReaction(MessageReactionRequest request) {
    String currentClientId = getCurrentClientId();

    MessageDto message = getMessageOrThrow(request.getMessageId());
    validateParticipant(message.getConversationId(), currentClientId);

    Boolean exists =
        reactionRepository.exists(request.getMessageId(), currentClientId, request.getReaction());
    if (Boolean.TRUE.equals(exists)) {
      throw ExceptionFactory.badRequest(MessageConstant.E_MSG_010);
    }

    MessageReaction reaction =
        MessageReaction.builder()
            .messageId(request.getMessageId())
            .employeeId(currentClientId)
            .reaction(request.getReaction())
            .build();

    reactionRepository.insert(reaction);

    // Directly fetch the inserted reaction instead of filtering from all reactions
    MessageReactionDto addedReaction =
        reactionRepository.findByMessageIdAndEmployeeIdAndReaction(
            request.getMessageId(), currentClientId, request.getReaction());

    // Publish real-time event for reaction added
    if (addedReaction != null) {
      List<String> targetUserIds =
          participantRepository.findEmployeeIdsByConversationId(message.getConversationId());
      messagePublisher.publishReactionAdded(
          message.getConversationId(), addedReaction, targetUserIds);
    }

    return addedReaction;
  }

  /**
   * Removes a reaction from a message.
   *
   * @param request the reaction request
   */
  @Override
  @Transactional
  public void removeReaction(MessageReactionRequest request) {
    String currentClientId = getCurrentClientId();

    // Get the message to find the conversation ID for the real-time event
    MessageDto message = getMessageOrThrow(request.getMessageId());

    int rowsAffected =
        reactionRepository.softDelete(
            request.getMessageId(), currentClientId, request.getReaction());
    if (rowsAffected == CommonConstant.INIT_INDEX) {
      throw ExceptionFactory.badRequest(MessageConstant.E_MSG_014);
    }

    // Publish real-time event for reaction removed
    List<String> targetUserIds =
        participantRepository.findEmployeeIdsByConversationId(message.getConversationId());
    messagePublisher.publishReactionRemoved(
        message.getConversationId(),
        request.getMessageId(),
        currentClientId,
        request.getReaction(),
        targetUserIds);
  }

  /**
   * Gets reactions for a message.
   *
   * @param messageId the message ID
   * @return list of message reactions
   */
  @Override
  public List<MessageReactionDto> getReactions(String messageId) {
    MessageDto message = getMessageOrThrow(messageId);

    validateParticipant(message.getConversationId(), getCurrentClientId());

    return reactionRepository.findByMessageId(messageId);
  }

  /**
   * Validates that the employee is a participant in the conversation.
   *
   * @param conversationId the conversation ID
   * @param employeeId the employee ID
   */
  private void validateParticipant(String conversationId, String employeeId) {
    Boolean isParticipant =
        participantRepository.existsByConversationIdAndEmployeeId(conversationId, employeeId);
    if (!Boolean.TRUE.equals(isParticipant)) {
      throw ExceptionFactory.badRequest(MessageConstant.E_MSG_007);
    }
  }

  /**
   * Gets a message by ID and validates that it exists.
   *
   * @param messageId the message ID
   * @return the message DTO
   * @throws RuntimeException if message is not found
   */
  private MessageDto getMessageOrThrow(String messageId) {
    MessageDto message = messageRepository.findByMessageId(messageId);
    if (message == null) {
      throw ExceptionFactory.notFound(MessageConstant.E_MSG_008);
    }
    return message;
  }

  /**
   * Upload files for a message in a conversation.
   *
   * @param conversationId the conversation ID
   * @param files the files to upload
   * @return list of file metadata DTOs
   */
  @Override
  public List<FileMetadataDto> uploadMessageFiles(
      String conversationId, List<MultipartFile> files) {

    validateParticipant(conversationId, getCurrentClientId());

    return messageBlobAdapter.uploadMessageFiles(files, conversationId);
  }

  /**
   * Download a single file from a message.
   *
   * @param conversationId the conversation ID for access validation
   * @param request the file download request containing file path
   * @param outputStream the output stream to write file content
   */
  @Override
  public void downloadFile(
      String conversationId, FileDownloadRequest request, OutputStream outputStream) {
    validateParticipant(conversationId, getCurrentClientId());
    messageBlobAdapter.downloadFile(request.getFilePath(), outputStream);
  }

  /**
   * Download multiple files as a ZIP archive.
   *
   * @param conversationId the conversation ID for access validation
   * @param requests the list of file download requests
   * @param outputStream the output stream to write ZIP content
   */
  @Override
  public void downloadFilesAsZip(
      String conversationId, List<FileDownloadRequest> requests, OutputStream outputStream) {

    validateParticipant(conversationId, getCurrentClientId());

    List<String> filePaths = requests.stream().map(FileDownloadRequest::getFilePath).toList();
    messageBlobAdapter.downloadFilesAsZip(filePaths, outputStream);
  }

  /**
   * Get the original file name from a file download request.
   *
   * @param request the file download request
   * @return the original file name
   */
  @Override
  public String getOriginalFileName(FileDownloadRequest request) {
    return messageBlobAdapter.getOriginalFileName(request.getFilePath());
  }

  /**
   * Get the content type (MIME type) of a file from a file download request.
   *
   * @param request the file download request
   * @return the content type of the file
   */
  @Override
  public String getContentType(FileDownloadRequest request) {
    return messageBlobAdapter.getContentType(request.getFilePath());
  }

  /**
   * Get file download information including file name and content type in a single call.
   * Consolidates getOriginalFileName and getContentType to reduce service calls.
   *
   * @param request the file download request
   * @return FileDownloadInfoDto containing file name and content type
   */
  @Override
  public FileDownloadInfoDto getFileDownloadInfo(FileDownloadRequest request) {
    return messageBlobAdapter.getFileDownloadInfo(request.getFilePath());
  }

  /**
   * Get the current client ID from user content.
   *
   * @return the current client ID
   */
  private String getCurrentClientId() {
    return userContentProvider.getUserContent().getClientId();
  }

  /**
   * Refresh file URLs in message metadata by regenerating SAS tokens. SAS tokens expire after a
   * period, so we need to regenerate them when retrieving messages.
   *
   * @param message the message DTO to refresh file URLs for
   */
  private void refreshFileUrls(MessageDto message) {
    if (message == null || message.getMetadata() == null || message.getMetadata().isEmpty()) {
      return;
    }

    for (FileMetadataDto metadata : message.getMetadata()) {
      if (metadata.getFilePath() != null && !metadata.getFilePath().isEmpty()) {
        String freshUrl = messageBlobAdapter.generateSasToken(metadata.getFilePath());
        metadata.setFileUrl(freshUrl);
      }
    }
  }

  /**
   * Populate reactions for a message by fetching from the repository.
   *
   * @param message the message DTO to populate reactions for
   */
  private void populateReactions(MessageDto message) {
    if (message == null || message.getMessageId() == null) {
      return;
    }
    List<MessageReactionDto> reactions = reactionRepository.findByMessageId(message.getMessageId());
    message.setReactions(reactions);
  }
}
