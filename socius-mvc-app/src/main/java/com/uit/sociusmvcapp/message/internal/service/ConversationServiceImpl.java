package com.uit.sociusmvcapp.message.internal.service;

import com.uit.sociusmvcapp.iam.UserContentProvider;
import com.uit.sociusmvcapp.message.ConversationService;
import com.uit.sociusmvcapp.message.dto.ConversationDto;
import com.uit.sociusmvcapp.message.dto.ConversationParticipantDto;
import com.uit.sociusmvcapp.message.dto.request.AddParticipantRequest;
import com.uit.sociusmvcapp.message.dto.request.CreateGroupConversationRequest;
import com.uit.sociusmvcapp.message.dto.request.GetOrCreateDirectConversationRequest;
import com.uit.sociusmvcapp.message.dto.request.UpdateConversationRequest;
import com.uit.sociusmvcapp.message.dto.request.UpdateParticipantSettingsRequest;
import com.uit.sociusmvcapp.message.enums.ConversationType;
import com.uit.sociusmvcapp.message.enums.ParticipantRole;
import com.uit.sociusmvcapp.message.internal.domain.Conversation;
import com.uit.sociusmvcapp.message.internal.domain.ConversationParticipant;
import com.uit.sociusmvcapp.message.internal.repository.ConversationParticipantRepository;
import com.uit.sociusmvcapp.message.internal.repository.ConversationRepository;
import com.uit.sociusmvcapp.shared.constants.CommonConstant;
import com.uit.sociusmvcapp.shared.constants.MessageConstant;
import com.uit.sociusmvcapp.shared.response.CursorResponse;
import com.uit.sociusmvcapp.shared.service.ExceptionFactory;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Implementation of ConversationService for Conversation-related operations. */
@Service
@Slf4j
@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService {

  private final ConversationRepository conversationRepository;
  private final ConversationParticipantRepository participantRepository;
  private final UserContentProvider userContentProvider;

  @Override
  @Transactional
  public ConversationDto getOrCreateDirectConversation(
      GetOrCreateDirectConversationRequest request) {
    String currentUserId = userContentProvider.getUserContent().getClientId();
    String targetUserId = request.getTargetEmployeeId();

    // Prevent creating conversation with self
    if (currentUserId.equals(targetUserId)) {
      throw ExceptionFactory.badRequest(MessageConstant.E_MSG_011);
    }

    // Check if direct conversation already exists between these two users
    ConversationDto existingConversation =
        conversationRepository.findDirectConversationBetweenUsers(currentUserId, targetUserId);

    if (existingConversation != null) {
      return existingConversation;
    }

    // Create new direct conversation
    String conversationId = UUID.randomUUID().toString();

    Conversation conversation =
        Conversation.builder()
            .conversationId(conversationId)
            .type(ConversationType.DIRECT.getCode())
            .createdBy(currentUserId)
            .build();

    conversationRepository.insert(conversation);

    // Add both users as participants
    List<ConversationParticipant> participants = new ArrayList<>();
    participants.add(
        ConversationParticipant.builder()
            .conversationId(conversationId)
            .employeeId(currentUserId)
            .role(ParticipantRole.MEMBER.getCode())
            .build());
    participants.add(
        ConversationParticipant.builder()
            .conversationId(conversationId)
            .employeeId(targetUserId)
            .role(ParticipantRole.MEMBER.getCode())
            .build());

    participantRepository.insertBatch(participants);

    return conversationRepository.findByConversationId(conversationId);
  }

  @Override
  @Transactional
  public ConversationDto createGroupConversation(CreateGroupConversationRequest request) {
    String currentUserId = userContentProvider.getUserContent().getClientId();

    // Generate unique conversation ID
    String conversationId = UUID.randomUUID().toString();

    // Create group conversation entity
    Conversation conversation =
        Conversation.builder()
            .conversationId(conversationId)
            .type(ConversationType.GROUP.getCode())
            .name(request.getName())
            .avatarUrl(request.getAvatarUrl())
            .createdBy(currentUserId)
            .build();

    conversationRepository.insert(conversation);

    // Add creator as OWNER participant
    List<ConversationParticipant> participants = new ArrayList<>();
    participants.add(
        ConversationParticipant.builder()
            .conversationId(conversationId)
            .employeeId(currentUserId)
            .role(ParticipantRole.OWNER.getCode())
            .build());

    // Add other participants as MEMBER
    for (String participantId : request.getParticipantIds()) {
      if (!participantId.equals(currentUserId)) {
        participants.add(
            ConversationParticipant.builder()
                .conversationId(conversationId)
                .employeeId(participantId)
                .role(ParticipantRole.MEMBER.getCode())
                .build());
      }
    }

    participantRepository.insertBatch(participants);

    return conversationRepository.findByConversationId(conversationId);
  }

  @Override
  public ConversationDto getByConversationId(String conversationId) {
    String currentUserId = userContentProvider.getUserContent().getClientId();

    // Verify user is a participant
    validateParticipant(conversationId, currentUserId);

    ConversationDto conversation = conversationRepository.findByConversationId(conversationId);
    if (conversation == null) {
      throw ExceptionFactory.notFound(MessageConstant.E_MSG_002);
    }
    return conversation;
  }

  @Override
  @Transactional
  public ConversationDto update(String conversationId, UpdateConversationRequest request) {
    String currentUserId = userContentProvider.getUserContent().getClientId();

    // Verify user is a participant
    validateParticipant(conversationId, currentUserId);

    Conversation existingConversation =
        conversationRepository.findEntityByConversationId(conversationId);
    if (existingConversation == null) {
      throw ExceptionFactory.notFound(MessageConstant.E_MSG_002);
    }

    Conversation updateData =
        Conversation.builder().name(request.getName()).avatarUrl(request.getAvatarUrl()).build();

    conversationRepository.update(conversationId, updateData);

    return conversationRepository.findByConversationId(conversationId);
  }

  @Override
  @Transactional
  public void delete(String conversationId) {
    String currentUserId = userContentProvider.getUserContent().getClientId();

    // Verify user is the owner
    Conversation conversation = conversationRepository.findEntityByConversationId(conversationId);
    if (conversation == null) {
      throw ExceptionFactory.notFound(MessageConstant.E_MSG_002);
    }

    if (!conversation.getCreatedBy().equals(currentUserId)) {
      throw ExceptionFactory.badRequest(MessageConstant.E_MSG_003);
    }

    conversationRepository.softDelete(conversationId);
  }

  @Override
  public CursorResponse<ConversationDto> getConversations(String cursor, int limit) {
    String currentUserId = userContentProvider.getUserContent().getClientId();

    LocalDateTime lastMessageAt = null;
    Long lastId = null;

    // Decode cursor if provided
    if (cursor != null && !cursor.isEmpty()) {
      try {
        String decoded = new String(Base64.getDecoder().decode(cursor));
        String[] parts = decoded.split(CommonConstant.UNDERSCORE);
        if (parts.length >= 2) {
          lastMessageAt = LocalDateTime.parse(parts[CommonConstant.INIT_INDEX]);
          lastId = Long.parseLong(parts[CommonConstant.ONE]);
        }
      } catch (IllegalArgumentException | java.time.format.DateTimeParseException e) {
        log.warn("Invalid cursor format, ignoring cursor: {}", cursor);
        // Continue without cursor - will return first page
      }
    }

    List<ConversationDto> conversations =
        conversationRepository.findByEmployeeIdWithCursor(
            currentUserId, lastMessageAt, lastId, limit);

    // Prepare next cursor
    String nextCursor = null;
    if (!conversations.isEmpty()) {
      ConversationDto lastConversation =
          conversations.get(conversations.size() - CommonConstant.ONE);
      LocalDateTime cursorTime =
          lastConversation.getLastMessageAt() != null
              ? lastConversation.getLastMessageAt()
              : lastConversation.getCreatedAt();
      String cursorString =
          cursorTime.toString() + CommonConstant.UNDERSCORE + lastConversation.getId();
      nextCursor = Base64.getEncoder().encodeToString(cursorString.getBytes());
    }

    boolean hasNext = conversations.size() == limit;
    return CursorResponse.<ConversationDto>builder()
        .data(conversations)
        .nextCursor(nextCursor)
        .hasNext(hasNext)
        .build();
  }

  @Override
  public List<ConversationParticipantDto> getParticipants(String conversationId) {
    String currentUserId = userContentProvider.getUserContent().getClientId();

    // Verify user is a participant
    validateParticipant(conversationId, currentUserId);

    return participantRepository.findByConversationId(conversationId);
  }

  @Override
  @Transactional
  public ConversationParticipantDto addParticipant(
      String conversationId, AddParticipantRequest request) {
    String currentUserId = userContentProvider.getUserContent().getClientId();

    // Verify user is a participant
    validateParticipant(conversationId, currentUserId);

    // Verify this is a GROUP conversation (cannot add participants to DIRECT)
    Conversation conversation = conversationRepository.findEntityByConversationId(conversationId);
    if (conversation != null && ConversationType.DIRECT.getCode().equals(conversation.getType())) {
      throw ExceptionFactory.badRequest(MessageConstant.E_MSG_012);
    }

    // Check if employee is already a participant
    Boolean exists =
        participantRepository.existsByConversationIdAndEmployeeId(
            conversationId, request.getEmployeeId());
    if (Boolean.TRUE.equals(exists)) {
      throw ExceptionFactory.badRequest(MessageConstant.E_MSG_004);
    }

    String role = request.getRole() != null ? request.getRole() : ParticipantRole.MEMBER.getCode();

    ConversationParticipant participant =
        ConversationParticipant.builder()
            .conversationId(conversationId)
            .employeeId(request.getEmployeeId())
            .role(role)
            .build();

    participantRepository.insert(participant);

    return participantRepository.findByConversationIdAndEmployeeId(
        conversationId, request.getEmployeeId());
  }

  @Override
  @Transactional
  public void removeParticipant(String conversationId, String employeeId) {
    String currentUserId = userContentProvider.getUserContent().getClientId();

    // Verify user is a participant and has permission
    validateParticipant(conversationId, currentUserId);

    Conversation conversation = conversationRepository.findEntityByConversationId(conversationId);
    if (conversation != null && conversation.getCreatedBy().equals(employeeId)) {
      throw ExceptionFactory.badRequest(MessageConstant.E_MSG_005);
    }

    participantRepository.softDelete(conversationId, employeeId);
  }

  @Override
  @Transactional
  public void leaveConversation(String conversationId) {
    String currentUserId = userContentProvider.getUserContent().getClientId();

    // Check if user is the owner
    Conversation conversation = conversationRepository.findEntityByConversationId(conversationId);
    if (conversation != null && conversation.getCreatedBy().equals(currentUserId)) {
      throw ExceptionFactory.badRequest(MessageConstant.E_MSG_006);
    }

    participantRepository.softDelete(conversationId, currentUserId);
  }

  @Override
  @Transactional
  public void updateParticipantSettings(
      String conversationId, UpdateParticipantSettingsRequest request) {
    String currentUserId = userContentProvider.getUserContent().getClientId();

    // Verify user is a participant
    validateParticipant(conversationId, currentUserId);

    participantRepository.updateSettings(
        conversationId, currentUserId, request.getIsMuted(), request.getIsPinned());
  }

  @Override
  @Transactional
  public void markAsRead(String conversationId, Long messageId) {
    String currentUserId = userContentProvider.getUserContent().getClientId();

    // Verify user is a participant
    validateParticipant(conversationId, currentUserId);

    participantRepository.updateLastReadMessage(conversationId, currentUserId, messageId);
  }

  private void validateParticipant(String conversationId, String employeeId) {
    Boolean isParticipant =
        participantRepository.existsByConversationIdAndEmployeeId(conversationId, employeeId);
    if (!Boolean.TRUE.equals(isParticipant)) {
      throw ExceptionFactory.badRequest(MessageConstant.E_MSG_007);
    }
  }
}
