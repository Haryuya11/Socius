package com.uit.sociusmvcapp.message.internal.repository;

import com.uit.sociusmvcapp.message.dto.ConversationDto;
import com.uit.sociusmvcapp.message.internal.converter.ConversationConverter;
import com.uit.sociusmvcapp.message.internal.domain.Conversation;
import com.uit.sociusmvcapp.message.internal.persistence.ConversationMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/** Repository for Conversation entity. */
@Repository
@RequiredArgsConstructor
public class ConversationRepository {

  private final ConversationMapper conversationMapper;
  private final ConversationConverter conversationConverter;

  /**
   * Insert a new conversation.
   *
   * @param conversation the conversation to insert
   */
  public void insert(Conversation conversation) {
    conversationMapper.insert(conversation);
  }

  /**
   * Find a conversation by its unique conversation ID.
   *
   * @param conversationId the unique conversation ID
   * @return the conversation DTO if found, null otherwise
   */
  public ConversationDto findByConversationId(String conversationId) {
    Conversation conversation = conversationMapper.findByConversationId(conversationId);
    return conversationConverter.entityToDto(conversation);
  }

  /**
   * Find conversation entity by its unique conversation ID.
   *
   * @param conversationId the unique conversation ID
   * @return the conversation entity if found, null otherwise
   */
  public Conversation findEntityByConversationId(String conversationId) {
    return conversationMapper.findByConversationId(conversationId);
  }

  /**
   * Update a conversation.
   *
   * @param conversationId the unique conversation ID
   * @param conversation the updated conversation data
   */
  public void update(String conversationId, Conversation conversation) {
    conversationMapper.update(conversationId, conversation);
  }

  /**
   * Soft delete a conversation.
   *
   * @param conversationId the unique conversation ID
   */
  public void softDelete(String conversationId) {
    conversationMapper.softDelete(conversationId);
  }

  /**
   * Get all conversations for an employee.
   *
   * @param employeeId the employee ID
   * @param limit the maximum number of results
   * @param offset the offset for pagination
   * @return the list of conversation DTOs
   */
  public List<ConversationDto> findByEmployeeId(String employeeId, Integer limit, Integer offset) {
    List<Conversation> conversations =
        conversationMapper.findByEmployeeId(employeeId, limit, offset);
    return conversationConverter.entitiesToDtos(conversations);
  }

  /**
   * Count conversations for an employee.
   *
   * @param employeeId the employee ID
   * @return the count of conversations
   */
  public Integer countByEmployeeId(String employeeId) {
    return conversationMapper.countByEmployeeId(employeeId);
  }

  /**
   * Update last message info for a conversation.
   *
   * @param conversationId the conversation ID
   * @param lastMessageId the last message ID
   */
  public void updateLastMessage(String conversationId, Long lastMessageId) {
    conversationMapper.updateLastMessage(conversationId, lastMessageId);
  }
}
