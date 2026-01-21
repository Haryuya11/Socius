package com.uit.sociusmvcapp.message;

import com.uit.sociusmvcapp.message.dto.MessageDto;
import com.uit.sociusmvcapp.message.dto.MessageReactionDto;
import java.util.List;

/** Service interface for publishing messages to real-time consumers. */
public interface MessagePublisher {

  /**
   * Publishes a new message event.
   *
   * @param message the message to be published
   * @param targetUserIds the list of user IDs to receive this event (conversation participants)
   */
  void publishNewMessage(MessageDto message, List<String> targetUserIds);

  /**
   * Publishes a message update event.
   *
   * @param message the updated message
   * @param targetUserIds the list of user IDs to receive this event (conversation participants)
   */
  void publishMessageUpdated(MessageDto message, List<String> targetUserIds);

  /**
   * Publishes a message deletion event.
   *
   * @param conversationId the conversation ID
   * @param messageId the deleted message ID
   * @param targetUserIds the list of user IDs to receive this event (conversation participants)
   */
  void publishMessageDeleted(String conversationId, String messageId, List<String> targetUserIds);

  /**
   * Publishes a typing indicator event.
   *
   * @param conversationId the conversation ID
   * @param employeeId the employee who is typing
   * @param isTyping whether the user is typing
   * @param targetUserIds the list of user IDs to receive this event (conversation participants)
   */
  void publishTypingIndicator(
      String conversationId, String employeeId, boolean isTyping, List<String> targetUserIds);

  /**
   * Publishes a reaction added event.
   *
   * @param conversationId the conversation ID
   * @param reaction the reaction that was added
   * @param targetUserIds the list of user IDs to receive this event (conversation participants)
   */
  void publishReactionAdded(
      String conversationId, MessageReactionDto reaction, List<String> targetUserIds);

  /**
   * Publishes a reaction removed event.
   *
   * @param conversationId the conversation ID
   * @param messageId the message ID
   * @param employeeId the employee who removed the reaction
   * @param reactionType the reaction type that was removed
   * @param targetUserIds the list of user IDs to receive this event (conversation participants)
   */
  void publishReactionRemoved(
      String conversationId,
      String messageId,
      String employeeId,
      String reactionType,
      List<String> targetUserIds);
}
