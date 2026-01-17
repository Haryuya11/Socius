package com.uit.sociusmvcapp.message.internal.repository;

import com.uit.sociusmvcapp.message.dto.ConversationParticipantDto;
import com.uit.sociusmvcapp.message.internal.converter.ConversationParticipantConverter;
import com.uit.sociusmvcapp.message.internal.domain.ConversationParticipant;
import com.uit.sociusmvcapp.message.internal.persistence.ConversationParticipantMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/** Repository for ConversationParticipant entity. */
@Repository
@RequiredArgsConstructor
public class ConversationParticipantRepository {

  private final ConversationParticipantMapper participantMapper;
  private final ConversationParticipantConverter participantConverter;

  /**
   * Insert a new participant.
   *
   * @param participant the participant to insert
   */
  public void insert(ConversationParticipant participant) {
    participantMapper.insert(participant);
  }

  /**
   * Insert multiple participants in batch.
   *
   * @param participants the list of participants to insert
   */
  public void insertBatch(List<ConversationParticipant> participants) {
    participantMapper.insertBatch(participants);
  }

  /**
   * Find a participant by conversation ID and employee ID.
   *
   * @param conversationId the conversation ID
   * @param employeeId the employee ID
   * @return the participant DTO if found, null otherwise
   */
  public ConversationParticipantDto findByConversationIdAndEmployeeId(
      String conversationId, String employeeId) {
    ConversationParticipant participant =
        participantMapper.findByConversationIdAndEmployeeId(conversationId, employeeId);
    return participantConverter.entityToDto(participant);
  }

  /**
   * Find all participants of a conversation.
   *
   * @param conversationId the conversation ID
   * @return the list of participant DTOs
   */
  public List<ConversationParticipantDto> findByConversationId(String conversationId) {
    List<ConversationParticipant> participants =
        participantMapper.findByConversationId(conversationId);
    return participantConverter.entitiesToDtos(participants);
  }

  /**
   * Get all employee IDs for participants in a conversation.
   *
   * @param conversationId the conversation ID
   * @return the list of employee IDs
   */
  public List<String> findEmployeeIdsByConversationId(String conversationId) {
    List<ConversationParticipant> participants =
        participantMapper.findByConversationId(conversationId);
    return participants.stream().map(ConversationParticipant::getEmployeeId).toList();
  }

  /**
   * Update participant settings (mute, pin).
   *
   * @param conversationId the conversation ID
   * @param employeeId the employee ID
   * @param isMuted whether the conversation is muted
   * @param isPinned whether the conversation is pinned
   */
  public void updateSettings(
      String conversationId, String employeeId, Boolean isMuted, Boolean isPinned) {
    participantMapper.updateSettings(conversationId, employeeId, isMuted, isPinned);
  }

  /**
   * Update last read message for a participant.
   *
   * @param conversationId the conversation ID
   * @param employeeId the employee ID
   * @param lastReadMessageId the last read message ID
   */
  public void updateLastReadMessage(
      String conversationId, String employeeId, Long lastReadMessageId) {
    participantMapper.updateLastReadMessage(conversationId, employeeId, lastReadMessageId);
  }

  /**
   * Soft delete a participant (leave conversation).
   *
   * @param conversationId the conversation ID
   * @param employeeId the employee ID
   */
  public void softDelete(String conversationId, String employeeId) {
    participantMapper.softDelete(conversationId, employeeId);
  }

  /**
   * Check if an employee is a participant in a conversation.
   *
   * @param conversationId the conversation ID
   * @param employeeId the employee ID
   * @return true if participant exists
   */
  public Boolean existsByConversationIdAndEmployeeId(String conversationId, String employeeId) {
    return participantMapper.existsByConversationIdAndEmployeeId(conversationId, employeeId);
  }
}
