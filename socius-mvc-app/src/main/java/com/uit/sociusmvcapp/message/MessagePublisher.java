package com.uit.sociusmvcapp.message;

import com.uit.sociusmvcapp.message.dto.MessageDto;

/** Service interface for publishing messages to real-time consumers. */
public interface MessagePublisher {

  /**
   * Publishes a new message event.
   *
   * @param message the message to be published
   */
  void publishNewMessage(MessageDto message);

  /**
   * Publishes a message update event.
   *
   * @param message the updated message
   */
  void publishMessageUpdated(MessageDto message);

  /**
   * Publishes a message deletion event.
   *
   * @param conversationId the conversation ID
   * @param messageId the deleted message ID
   */
  void publishMessageDeleted(String conversationId, String messageId);

  /**
   * Publishes a typing indicator event.
   *
   * @param conversationId the conversation ID
   * @param employeeId the employee who is typing
   * @param isTyping whether the user is typing
   */
  void publishTypingIndicator(String conversationId, String employeeId, boolean isTyping);
}
