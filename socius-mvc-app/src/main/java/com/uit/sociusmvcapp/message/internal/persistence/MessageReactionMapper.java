package com.uit.sociusmvcapp.message.internal.persistence;

import com.uit.sociusmvcapp.message.internal.domain.MessageReaction;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/** MyBatis Mapper interface for MessageReaction entity. */
@Mapper
public interface MessageReactionMapper {

  /**
   * Insert a new reaction.
   *
   * @param reaction the reaction to insert
   */
  void insert(MessageReaction reaction);

  /**
   * Find all reactions for a message.
   *
   * @param messageId the message ID
   * @return the list of reactions
   */
  List<MessageReaction> findByMessageId(String messageId);

  /**
   * Delete a specific reaction.
   *
   * @param messageId the message ID
   * @param employeeId the employee ID
   * @param reaction the reaction type
   */
  void softDelete(
      @Param("messageId") String messageId,
      @Param("employeeId") String employeeId,
      @Param("reaction") String reaction);

  /**
   * Check if a reaction exists.
   *
   * @param messageId the message ID
   * @param employeeId the employee ID
   * @param reaction the reaction type
   * @return true if the reaction exists
   */
  Boolean exists(
      @Param("messageId") String messageId,
      @Param("employeeId") String employeeId,
      @Param("reaction") String reaction);
}
