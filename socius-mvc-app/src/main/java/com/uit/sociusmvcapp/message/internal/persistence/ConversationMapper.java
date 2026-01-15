package com.uit.sociusmvcapp.message.internal.persistence;

import com.uit.sociusmvcapp.message.internal.domain.Conversation;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/** MyBatis Mapper interface for Conversation entity. */
@Mapper
public interface ConversationMapper {

  /**
   * Insert a new conversation.
   *
   * @param conversation the conversation to insert
   */
  void insert(Conversation conversation);

  /**
   * Find a conversation by its unique conversation ID.
   *
   * @param conversationId the unique conversation ID
   * @return the conversation if found, null otherwise
   */
  Conversation findByConversationId(String conversationId);

  /**
   * Update a conversation.
   *
   * @param conversationId the unique conversation ID
   * @param conversation the updated conversation data
   */
  void update(
      @Param("conversationId") String conversationId,
      @Param("conversation") Conversation conversation);

  /**
   * Soft delete a conversation.
   *
   * @param conversationId the unique conversation ID
   */
  void softDelete(String conversationId);

  /**
   * Get all conversations for an employee.
   *
   * @param employeeId the employee ID
   * @param limit the maximum number of results
   * @param offset the offset for pagination
   * @return the list of conversations
   */
  List<Conversation> findByEmployeeId(
      @Param("employeeId") String employeeId,
      @Param("limit") Integer limit,
      @Param("offset") Integer offset);

  /**
   * Count conversations for an employee.
   *
   * @param employeeId the employee ID
   * @return the count of conversations
   */
  Integer countByEmployeeId(String employeeId);

  /**
   * Update last message info for a conversation.
   *
   * @param conversationId the conversation ID
   * @param lastMessageId the last message ID
   */
  void updateLastMessage(
      @Param("conversationId") String conversationId, @Param("lastMessageId") Long lastMessageId);

  /**
   * Find an existing DIRECT conversation between two users.
   *
   * @param employeeId1 the first employee ID
   * @param employeeId2 the second employee ID
   * @return the conversation if found, null otherwise
   */
  Conversation findDirectConversationBetweenUsers(
      @Param("employeeId1") String employeeId1, @Param("employeeId2") String employeeId2);

  /**
   * Get all conversations for an employee with cursor-based pagination.
   *
   * @param employeeId the employee ID
   * @param lastMessageAt the last message timestamp for cursor
   * @param lastId the last conversation ID for cursor
   * @param limit the maximum number of results
   * @return the list of conversations
   */
  List<Conversation> findByEmployeeIdWithCursor(
      @Param("employeeId") String employeeId,
      @Param("lastMessageAt") java.time.LocalDateTime lastMessageAt,
      @Param("lastId") Long lastId,
      @Param("limit") int limit);
}
