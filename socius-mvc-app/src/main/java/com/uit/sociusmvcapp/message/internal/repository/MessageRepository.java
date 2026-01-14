package com.uit.sociusmvcapp.message.internal.repository;

import com.uit.sociusmvcapp.message.dto.MessageDto;
import com.uit.sociusmvcapp.message.internal.converter.MessageConverter;
import com.uit.sociusmvcapp.message.internal.domain.Message;
import com.uit.sociusmvcapp.message.internal.persistence.MessageMapper;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/** Repository for Message entity. */
@Repository
@RequiredArgsConstructor
public class MessageRepository {

  private final MessageMapper messageMapper;
  private final MessageConverter messageConverter;

  /**
   * Insert a new message.
   *
   * @param message the message to insert
   */
  public void insert(Message message) {
    messageMapper.insert(message);
  }

  /**
   * Find a message by its unique message ID.
   *
   * @param messageId the unique message ID
   * @return the message DTO if found, null otherwise
   */
  public MessageDto findByMessageId(String messageId) {
    Message message = messageMapper.findByMessageId(messageId);
    return messageConverter.entityToDto(message);
  }

  /**
   * Find message entity by its unique message ID.
   *
   * @param messageId the unique message ID
   * @return the message entity if found, null otherwise
   */
  public Message findEntityByMessageId(String messageId) {
    return messageMapper.findByMessageId(messageId);
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
    return messageConverter.entitiesToDtos(messages);
  }

  /**
   * Update message content.
   *
   * @param messageId the message ID
   * @param content the new content
   */
  public void updateContent(String messageId, String content) {
    messageMapper.updateContent(messageId, content);
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
   * @param lastReadMessageId the last read message ID
   * @return the count of unread messages
   */
  public Integer countUnreadMessages(String conversationId, Long lastReadMessageId) {
    return messageMapper.countUnreadMessages(conversationId, lastReadMessageId);
  }
}
