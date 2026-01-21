package com.uit.sociusmvcapp.message.internal.repository;

import com.uit.sociusmvcapp.message.dto.MessageDto;
import com.uit.sociusmvcapp.message.dto.request.SendMessageRequest;
import com.uit.sociusmvcapp.message.internal.component.MessageContentEncryptor;
import com.uit.sociusmvcapp.message.internal.converter.MessageConverter;
import com.uit.sociusmvcapp.message.internal.domain.Message;
import com.uit.sociusmvcapp.message.internal.persistence.MessageMapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/** Repository for Message entity. */
@Repository
@RequiredArgsConstructor
public class MessageRepository {

  private final MessageMapper messageMapper;
  private final MessageConverter messageConverter;
  private final MessageContentEncryptor contentEncryptor;

  /**
   * Insert a new message.
   *
   * @param request the send message request
   * @param messageId the unique message ID
   * @param senderId the sender ID
   */
  public void insert(SendMessageRequest request, String messageId, String senderId) {
    messageMapper.insert(
        messageConverter.sendRequestToEntity(request, messageId, senderId, contentEncryptor));
  }

  /**
   * Create a new message and return its DTO.
   *
   * @param request the send message request
   * @param senderId the sender ID
   * @return the created message DTO
   */
  public MessageDto createMessage(SendMessageRequest request, String senderId) {
    String messageId = UUID.randomUUID().toString();
    insert(request, messageId, senderId);
    return findByMessageId(messageId);
  }

  /**
   * Find a message by its unique message ID.
   *
   * @param messageId the unique message ID
   * @return the message DTO if found, null otherwise
   */
  public MessageDto findByMessageId(String messageId) {
    return messageConverter.entityToDto(messageMapper.findByMessageId(messageId), contentEncryptor);
  }

  /**
   * Get messages in a conversation with cursor-based pagination.
   *
   * @param conversationId the conversation ID
   * @param lastCreatedAt the 'created_at' of the last item in the previous list
   * @param lastId the 'id' of the last item in the previous list
   * @param limit the number of items to retrieve
   * @return the list of message DTOs
   */
  public List<MessageDto> findByConversationId(
      String conversationId, LocalDateTime lastCreatedAt, Long lastId, int limit) {
    List<Message> messages =
        messageMapper.findByConversationId(conversationId, lastCreatedAt, lastId, limit);
    List<MessageDto> dtos = messageConverter.entitiesToDtos(messages, contentEncryptor);

    // Transform deleted messages to placeholder
    for (int i = 0; i < messages.size(); i++) {
      messageConverter.transformIfDeleted(messages.get(i), dtos.get(i));
    }

    return dtos;
  }

  /**
   * Update message content.
   *
   * @param messageId the message ID
   * @param content the new content (plaintext - will be encrypted)
   */
  public void updateContent(String messageId, String content) {
    String encryptedContent = contentEncryptor.encryptContent(content);
    messageMapper.updateContent(messageId, encryptedContent);
  }

  /**
   * Soft delete a message.
   *
   * @param messageId the message ID
   */
  public void softDelete(String messageId) {
    messageMapper.softDelete(messageId);
  }

  /**
   * Count unread messages for a participant.
   *
   * @param conversationId the conversation ID
   * @param employeeId the employee ID
   * @return the count of unread messages
   */
  public Integer countUnreadMessages(String conversationId, String employeeId) {
    return messageMapper.countUnreadMessages(conversationId, employeeId);
  }
}
