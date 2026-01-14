package com.uit.sociusmvcapp.message.internal.persistence;

import com.uit.sociusmvcapp.message.internal.domain.ConversationParticipant;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/** MyBatis Mapper interface for ConversationParticipant entity. */
@Mapper
public interface ConversationParticipantMapper {

  /**
   * Insert a new participant.
   *
   * @param participant the participant to insert
   */
  void insert(ConversationParticipant participant);

  /**
   * Insert multiple participants in batch.
   *
   * @param participants the list of participants to insert
   */
  void insertBatch(@Param("participants") List<ConversationParticipant> participants);

  /**
   * Find a participant by conversation ID and employee ID.
   *
   * @param conversationId the conversation ID
   * @param employeeId the employee ID
   * @return the participant if found, null otherwise
   */
  ConversationParticipant findByConversationIdAndEmployeeId(
      @Param("conversationId") String conversationId, @Param("employeeId") String employeeId);

  /**
   * Find all participants of a conversation.
   *
   * @param conversationId the conversation ID
   * @return the list of participants
   */
  List<ConversationParticipant> findByConversationId(String conversationId);

  /**
   * Update participant settings (mute, pin).
   *
   * @param conversationId the conversation ID
   * @param employeeId the employee ID
   * @param isMuted whether the conversation is muted
   * @param isPinned whether the conversation is pinned
   */
  void updateSettings(
      @Param("conversationId") String conversationId,
      @Param("employeeId") String employeeId,
      @Param("isMuted") Boolean isMuted,
      @Param("isPinned") Boolean isPinned);

  /**
   * Update last read message for a participant.
   *
   * @param conversationId the conversation ID
   * @param employeeId the employee ID
   * @param lastReadMessageId the last read message ID
   */
  void updateLastReadMessage(
      @Param("conversationId") String conversationId,
      @Param("employeeId") String employeeId,
      @Param("lastReadMessageId") Long lastReadMessageId);

  /**
   * Soft delete a participant (leave conversation).
   *
   * @param conversationId the conversation ID
   * @param employeeId the employee ID
   */
  void softDelete(
      @Param("conversationId") String conversationId, @Param("employeeId") String employeeId);

  /**
   * Check if an employee is a participant in a conversation.
   *
   * @param conversationId the conversation ID
   * @param employeeId the employee ID
   * @return true if participant exists
   */
  Boolean existsByConversationIdAndEmployeeId(
      @Param("conversationId") String conversationId, @Param("employeeId") String employeeId);
}
