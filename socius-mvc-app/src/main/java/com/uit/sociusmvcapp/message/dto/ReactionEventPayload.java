package com.uit.sociusmvcapp.message.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Payload for reaction-related realtime events. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReactionEventPayload {

  /** The conversation ID. */
  private String conversationId;

  /** The message ID that the reaction is associated with. */
  private String messageId;

  /** The reaction data (for REACTION_ADDED). */
  private MessageReactionDto reaction;

  /** The employee ID who added/removed the reaction. */
  private String employeeId;

  /** The reaction type (emoji code) that was removed (for REACTION_REMOVED). */
  private String reactionType;

  /**
   * Creates a payload for reaction added events.
   *
   * @param conversationId the conversation ID
   * @param reaction the reaction that was added
   * @return the payload
   */
  public static ReactionEventPayload forReactionAdded(
      String conversationId, MessageReactionDto reaction) {
    return ReactionEventPayload.builder()
        .conversationId(conversationId)
        .messageId(reaction.getMessageId())
        .reaction(reaction)
        .employeeId(reaction.getEmployeeId())
        .build();
  }

  /**
   * Creates a payload for reaction removed events.
   *
   * @param conversationId the conversation ID
   * @param messageId the message ID
   * @param employeeId the employee who removed the reaction
   * @param reactionType the reaction type that was removed
   * @return the payload
   */
  public static ReactionEventPayload forReactionRemoved(
      String conversationId, String messageId, String employeeId, String reactionType) {
    return ReactionEventPayload.builder()
        .conversationId(conversationId)
        .messageId(messageId)
        .employeeId(employeeId)
        .reactionType(reactionType)
        .build();
  }
}
