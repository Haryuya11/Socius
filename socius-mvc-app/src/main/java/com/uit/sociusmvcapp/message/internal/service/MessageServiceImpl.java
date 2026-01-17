package com.uit.sociusmvcapp.message.internal.service;

import com.uit.sociusmvcapp.iam.UserContentProvider;
import com.uit.sociusmvcapp.message.MessagePublisher;
import com.uit.sociusmvcapp.message.MessageService;
import com.uit.sociusmvcapp.message.dto.MessageDto;
import com.uit.sociusmvcapp.message.dto.MessageReactionDto;
import com.uit.sociusmvcapp.message.dto.request.MessageReactionRequest;
import com.uit.sociusmvcapp.message.dto.request.SendMessageRequest;
import com.uit.sociusmvcapp.message.dto.request.UpdateMessageRequest;
import com.uit.sociusmvcapp.message.internal.converter.MessageConverter;
import com.uit.sociusmvcapp.message.internal.domain.Message;
import com.uit.sociusmvcapp.message.internal.domain.MessageReaction;
import com.uit.sociusmvcapp.message.internal.repository.ConversationParticipantRepository;
import com.uit.sociusmvcapp.message.internal.repository.ConversationRepository;
import com.uit.sociusmvcapp.message.internal.repository.MessageReactionRepository;
import com.uit.sociusmvcapp.message.internal.repository.MessageRepository;
import com.uit.sociusmvcapp.shared.constants.CommonConstant;
import com.uit.sociusmvcapp.shared.constants.MessageConstant;
import com.uit.sociusmvcapp.shared.response.CursorResponse;
import com.uit.sociusmvcapp.shared.service.ExceptionFactory;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Implementation of MessageService for Message-related operations. */
@Service
@Slf4j
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

  private final MessageRepository messageRepository;
  private final MessageReactionRepository reactionRepository;
  private final ConversationRepository conversationRepository;
  private final ConversationParticipantRepository participantRepository;
  private final MessageConverter messageConverter;
  private final MessagePublisher messagePublisher;
  private final UserContentProvider userContentProvider;

  @Override
  @Transactional
  public MessageDto sendMessage(SendMessageRequest request) {
    String currentUserId = userContentProvider.getUserContent().getClientId();

    // Verify user is a participant
    validateParticipant(request.getConversationId(), currentUserId);

    // Generate unique message ID
    String messageId = UUID.randomUUID().toString();

    // Convert metadata to JSON
    String metadataJson = messageConverter.metadataToJson(request.getMetadata());

    // Create message entity
    Message message =
        Message.builder()
            .messageId(messageId)
            .conversationId(request.getConversationId())
            .senderId(currentUserId)
            .content(request.getContent())
            .messageType(request.getMessageType())
            .parentMessageId(request.getParentMessageId())
            .metadataJson(metadataJson)
            .build();

    messageRepository.insert(message);

    // Update conversation's last message
    if (message.getId() != null) {
      conversationRepository.updateLastMessage(
          request.getConversationId(), message.getId().longValue());
    }

    // Get the created message DTO
    MessageDto messageDto = messageRepository.findByMessageId(messageId);

    // Get participants for realtime event
    List<String> targetUserIds =
        participantRepository.findEmployeeIdsByConversationId(request.getConversationId());

    // Publish real-time event
    messagePublisher.publishNewMessage(messageDto, targetUserIds);

    return messageDto;
  }

  @Override
  public MessageDto getByMessageId(String messageId) {
    String currentUserId = userContentProvider.getUserContent().getClientId();

    MessageDto message = messageRepository.findByMessageId(messageId);
    if (message == null) {
      throw ExceptionFactory.notFound(MessageConstant.E_MSG_008);
    }

    // Verify user is a participant
    validateParticipant(message.getConversationId(), currentUserId);

    return message;
  }

  @Override
  public CursorResponse<MessageDto> getMessages(String conversationId, String cursor, int limit) {
    String currentUserId = userContentProvider.getUserContent().getClientId();

    // Verify user is a participant
    validateParticipant(conversationId, currentUserId);

    LocalDateTime lastCreatedAt = null;
    Long lastId = null;

    // Decode cursor if provided
    if (cursor != null && !cursor.isEmpty()) {
      String decoded = new String(Base64.getDecoder().decode(cursor));
      String[] parts = decoded.split(CommonConstant.UNDERSCORE);
      lastCreatedAt = LocalDateTime.parse(parts[CommonConstant.INIT_INDEX]);
      lastId = Long.parseLong(parts[CommonConstant.ONE]);
    }

    List<MessageDto> messages =
        messageRepository.findByConversationId(conversationId, lastCreatedAt, lastId, limit);

    // Prepare next cursor
    String nextCursor = null;
    if (!messages.isEmpty()) {
      MessageDto lastMessage = messages.get(messages.size() - CommonConstant.ONE);
      String cursorString =
          lastMessage.getCreatedAt().toString() + CommonConstant.UNDERSCORE + lastMessage.getId();
      nextCursor = Base64.getEncoder().encodeToString(cursorString.getBytes());
    }

    boolean hasNext = messages.size() >= limit;
    return CursorResponse.<MessageDto>builder()
        .data(messages)
        .nextCursor(nextCursor)
        .hasNext(hasNext)
        .build();
  }

  @Override
  @Transactional
  public MessageDto updateMessage(String messageId, UpdateMessageRequest request) {
    String currentUserId = userContentProvider.getUserContent().getClientId();

    Message message = messageRepository.findEntityByMessageId(messageId);
    if (message == null) {
      throw ExceptionFactory.notFound(MessageConstant.E_MSG_008);
    }

    // Verify user is the sender
    if (!message.getSenderId().equals(currentUserId)) {
      throw ExceptionFactory.badRequest(MessageConstant.E_MSG_009);
    }

    messageRepository.updateContent(messageId, request.getContent());

    MessageDto updatedMessage = messageRepository.findByMessageId(messageId);

    // Get participants for realtime event
    List<String> targetUserIds =
        participantRepository.findEmployeeIdsByConversationId(message.getConversationId());

    // Publish real-time event
    messagePublisher.publishMessageUpdated(updatedMessage, targetUserIds);

    return updatedMessage;
  }

  @Override
  @Transactional
  public void deleteMessage(String messageId) {
    String currentUserId = userContentProvider.getUserContent().getClientId();

    Message message = messageRepository.findEntityByMessageId(messageId);
    if (message == null) {
      throw ExceptionFactory.notFound(MessageConstant.E_MSG_008);
    }

    // Verify user is the sender
    if (!message.getSenderId().equals(currentUserId)) {
      throw ExceptionFactory.badRequest(MessageConstant.E_MSG_009);
    }

    String conversationId = message.getConversationId();
    messageRepository.softDelete(messageId);

    // Get participants for realtime event
    List<String> targetUserIds =
        participantRepository.findEmployeeIdsByConversationId(conversationId);

    // Publish real-time event
    messagePublisher.publishMessageDeleted(conversationId, messageId, targetUserIds);
  }

  @Override
  @Transactional
  public MessageReactionDto addReaction(MessageReactionRequest request) {
    String currentUserId = userContentProvider.getUserContent().getClientId();

    Message message = messageRepository.findEntityByMessageId(request.getMessageId());
    if (message == null) {
      throw ExceptionFactory.notFound(MessageConstant.E_MSG_008);
    }

    // Verify user is a participant
    validateParticipant(message.getConversationId(), currentUserId);

    // Check if reaction already exists
    Boolean exists =
        reactionRepository.exists(request.getMessageId(), currentUserId, request.getReaction());
    if (Boolean.TRUE.equals(exists)) {
      throw ExceptionFactory.badRequest(MessageConstant.E_MSG_010);
    }

    MessageReaction reaction =
        MessageReaction.builder()
            .messageId(request.getMessageId())
            .employeeId(currentUserId)
            .reaction(request.getReaction())
            .build();

    reactionRepository.insert(reaction);

    List<MessageReactionDto> reactions = reactionRepository.findByMessageId(request.getMessageId());
    return reactions.stream()
        .filter(
            r ->
                r.getEmployeeId().equals(currentUserId)
                    && r.getReaction().equals(request.getReaction()))
        .findFirst()
        .orElse(null);
  }

  @Override
  @Transactional
  public void removeReaction(MessageReactionRequest request) {
    String currentUserId = userContentProvider.getUserContent().getClientId();

    reactionRepository.softDelete(request.getMessageId(), currentUserId, request.getReaction());
  }

  @Override
  public List<MessageReactionDto> getReactions(String messageId) {
    String currentUserId = userContentProvider.getUserContent().getClientId();

    Message message = messageRepository.findEntityByMessageId(messageId);
    if (message == null) {
      throw ExceptionFactory.notFound(MessageConstant.E_MSG_008);
    }

    // Verify user is a participant
    validateParticipant(message.getConversationId(), currentUserId);

    return reactionRepository.findByMessageId(messageId);
  }

  private void validateParticipant(String conversationId, String employeeId) {
    Boolean isParticipant =
        participantRepository.existsByConversationIdAndEmployeeId(conversationId, employeeId);
    if (!Boolean.TRUE.equals(isParticipant)) {
      throw ExceptionFactory.badRequest(MessageConstant.E_MSG_007);
    }
  }
}
