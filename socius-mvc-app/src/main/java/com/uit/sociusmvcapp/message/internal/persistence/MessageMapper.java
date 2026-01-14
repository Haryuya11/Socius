package com.uit.sociusmvcapp.message.internal.persistence;

import com.uit.sociusmvcapp.message.internal.domain.Message;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/** MyBatis Mapper interface for Message entity. */
@Mapper
public interface MessageMapper {

  /**
   * Insert a new message.
   *
   * @param message the message to insert
   */
  void insert(Message message);

  /**
   * Find a message by its unique message ID.
   *
   * @param messageId the unique message ID
   * @return the message if found, null otherwise
   */
  Message findByMessageId(String messageId);

  /**
   * Get messages in a conversation with cursor-based pagination.
   *
   * @param conversationId the conversation ID
   * @param lastCreatedAt the 'created_at' of the last item in the previous list
   * @param lastId the 'id' of the last item in the previous list
   * @param limit the number of items to retrieve
   * @return the list of messages
   */
  List<Message> findByConversationId(
      @Param("conversationId") String conversationId,
      @Param("lastCreatedAt") LocalDateTime lastCreatedAt,
      @Param("lastId") Long lastId,
      @Param("limit") int limit);

  /**
   * Update message content.
   *
   * @param messageId the message ID
   * @param content the new content
   */
  void updateContent(@Param("messageId") String messageId, @Param("content") String content);

  /**
   * Soft delete a message.
   *
   * @param messageId the message ID
   */
  void softDelete(String messageId);

  /**
   * Count unread messages for a participant.
   *
   * @param conversationId the conversation ID
   * @param lastReadMessageId the last read message ID
   * @return the count of unread messages
   */
  Integer countUnreadMessages(
      @Param("conversationId") String conversationId,
      @Param("lastReadMessageId") Long lastReadMessageId);
}
