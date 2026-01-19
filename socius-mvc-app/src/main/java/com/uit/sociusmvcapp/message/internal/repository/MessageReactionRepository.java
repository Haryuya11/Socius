package com.uit.sociusmvcapp.message.internal.repository;

import com.uit.sociusmvcapp.message.dto.MessageReactionDto;
import com.uit.sociusmvcapp.message.internal.converter.MessageReactionConverter;
import com.uit.sociusmvcapp.message.internal.domain.MessageReaction;
import com.uit.sociusmvcapp.message.internal.persistence.MessageReactionMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/** Repository for MessageReaction entity. */
@Repository
@RequiredArgsConstructor
public class MessageReactionRepository {

  private final MessageReactionMapper reactionMapper;
  private final MessageReactionConverter reactionConverter;

  /**
   * Insert a new reaction.
   *
   * @param reaction the reaction to insert
   */
  public void insert(MessageReaction reaction) {
    reactionMapper.insert(reaction);
  }

  /**
   * Find all reactions for a message.
   *
   * @param messageId the message ID
   * @return the list of reaction DTOs
   */
  public List<MessageReactionDto> findByMessageId(String messageId) {
    List<MessageReaction> reactions = reactionMapper.findByMessageId(messageId);
    return reactionConverter.entitiesToDtos(reactions);
  }

  /**
   * Delete a specific reaction.
   *
   * @param messageId the message ID
   * @param employeeId the employee ID
   * @param reaction the reaction type
   * @return number of rows affected
   */
  public int softDelete(String messageId, String employeeId, String reaction) {
    return reactionMapper.softDelete(messageId, employeeId, reaction);
  }

  /**
   * Check if a reaction exists.
   *
   * @param messageId the message ID
   * @param employeeId the employee ID
   * @param reaction the reaction type
   * @return true if the reaction exists
   */
  public Boolean exists(String messageId, String employeeId, String reaction) {
    return reactionMapper.exists(messageId, employeeId, reaction);
  }
}
