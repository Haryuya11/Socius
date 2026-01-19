package com.uit.sociusmvcapp.message.internal.service;

import com.uit.sociusmvcapp.iam.UserContentProvider;
import com.uit.sociusmvcapp.message.MessagePublisher;
import com.uit.sociusmvcapp.message.MessageService;
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
import java.util.ArrayList;
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
    String currentClientId = userContentProvider.getUserContent().getClientId();
    validateParticipant(request.getConversationId(), currentClientId);

    MessageDto messageDto = messageRepository.createMessage(request, currentClientId);

    // Update conversation's last message
    conversationRepository.updateLastMessage(
        request.getConversationId(), messageDto.getMessageId());

    // Get participants for realtime event
    List<String> targetUserIds =
        participantRepository.findEmployeeIdsByConversationId(request.getConversationId());

    // Publish real-time event
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
    String currentClientId = userContentProvider.getUserContent().getClientId();

    MessageDto message = messageRepository.findByMessageId(messageId);
    if (message == null) {
      throw ExceptionFactory.notFound(MessageConstant.E_MSG_008);
    }

    // Verify user is a participant
    validateParticipant(message.getConversationId(), currentClientId);

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
    String currentUserId = userContentProvider.getUserContent().getClientId();

    // Verify user is a participant
    validateParticipant(conversationId, currentUserId);

    LocalDateTime lastCreatedAt = null;
    Long lastId = null;

    // Decode cursor if provided
    if (cursor != null && !cursor.isEmpty()) {
      String decoded = new String(Base64.getDecoder().decode(cursor));
      String[] parts = decoded.split(CommonConstant.UNDERSCORE);
      lastCreatedAt = LocalDateTime.parse(parts[CommonConstant.INIT_INDEX]);
      lastId = Long.parseLong(parts[CommonConstant.ONE]);
    }

    List<MessageDto> messages =
        messageRepository.findByConversationId(conversationId, lastCreatedAt, lastId, limit);

    // Prepare next cursor
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
    String currentClientId = userContentProvider.getUserContent().getClientId();

    MessageDto message = messageRepository.findByMessageId(messageId);
    if (message == null) {
      throw ExceptionFactory.notFound(MessageConstant.E_MSG_008);
    }

    // Verify user is the sender
    if (!message.getSenderId().equals(currentClientId)) {
      throw ExceptionFactory.badRequest(MessageConstant.E_MSG_009);
    }

    messageRepository.updateContent(messageId, request.getContent());

    MessageDto updatedMessage = messageRepository.findByMessageId(messageId);

    // Get participants for realtime event
    List<String> targetUserIds =
        participantRepository.findEmployeeIdsByConversationId(message.getConversationId());

    // Publish real-time event
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
    String currentClientId = userContentProvider.getUserContent().getClientId();

    MessageDto message = messageRepository.findByMessageId(messageId);
    if (message == null) {
      throw ExceptionFactory.notFound(MessageConstant.E_MSG_008);
    }

    // Verify user is the sender
    if (!message.getSenderId().equals(currentClientId)) {
      throw ExceptionFactory.badRequest(MessageConstant.E_MSG_009);
    }

    String conversationId = message.getConversationId();
    messageRepository.softDelete(messageId);

    // Get participants for realtime event
    List<String> targetUserIds =
        participantRepository.findEmployeeIdsByConversationId(conversationId);

    // Publish real-time event
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
    String currentClientId = userContentProvider.getUserContent().getClientId();

    MessageDto message = messageRepository.findByMessageId(request.getMessageId());
    if (message == null) {
      throw ExceptionFactory.notFound(MessageConstant.E_MSG_008);
    }

    // Verify user is a participant
    validateParticipant(message.getConversationId(), currentClientId);

    // Check if reaction already exists
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

    List<MessageReactionDto> reactions = reactionRepository.findByMessageId(request.getMessageId());
    return reactions.stream()
        .filter(
            r ->
                r.getEmployeeId().equals(currentClientId)
                    && r.getReaction().equals(request.getReaction()))
        .findFirst()
        .orElse(null);
  }

  /**
   * Removes a reaction from a message.
   *
   * @param request the reaction request
   */
  @Override
  @Transactional
  public void removeReaction(MessageReactionRequest request) {
    String currentClientId = userContentProvider.getUserContent().getClientId();

    reactionRepository.softDelete(request.getMessageId(), currentClientId, request.getReaction());
  }

  /**
   * Gets reactions for a message.
   *
   * @param messageId the message ID
   * @return list of message reactions
   */
  @Override
  public List<MessageReactionDto> getReactions(String messageId) {
    String currentClientId = userContentProvider.getUserContent().getClientId();

    MessageDto message = messageRepository.findByMessageId(messageId);
    if (message == null) {
      throw ExceptionFactory.notFound(MessageConstant.E_MSG_008);
    }

    // Verify user is a participant
    validateParticipant(message.getConversationId(), currentClientId);

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
   * Upload files for a message in a conversation.
   *
   * @param conversationId the conversation ID
   * @param files the files to upload
   * @return list of file metadata DTOs
   */
  @Override
  public List<FileMetadataDto> uploadMessageFiles(
      String conversationId, List<MultipartFile> files) {
    String currentClientId = userContentProvider.getUserContent().getClientId();

    // Verify user is a participant
    validateParticipant(conversationId, currentClientId);

    List<FileMetadataDto> fileMetadataList = new ArrayList<>();
    for (MultipartFile file : files) {
      FileMetadataDto metadata = messageBlobAdapter.uploadMessageFile(file, conversationId);
      fileMetadataList.add(metadata);
    }

    return fileMetadataList;
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
    String currentClientId = userContentProvider.getUserContent().getClientId();

    // Verify user is a participant
    validateParticipant(conversationId, currentClientId);

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
    String currentClientId = userContentProvider.getUserContent().getClientId();

    // Verify user is a participant
    validateParticipant(conversationId, currentClientId);

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
}
