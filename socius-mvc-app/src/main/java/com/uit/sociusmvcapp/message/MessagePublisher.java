package com.uit.sociusmvcapp.message;

import com.uit.sociusmvcapp.message.dto.MessageDto;
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
}
