package com.uit.sociusmvcapp.message.internal.service;

import com.uit.sociusmvcapp.azure.blob.UploadFileDto;
import com.uit.sociusmvcapp.employee.EmployeeService;
import com.uit.sociusmvcapp.employee.dto.EmployeeDto;
import com.uit.sociusmvcapp.iam.UserContentProvider;
import com.uit.sociusmvcapp.message.ConversationService;
import com.uit.sociusmvcapp.message.dto.ConversationDto;
import com.uit.sociusmvcapp.message.dto.ConversationParticipantDto;
import com.uit.sociusmvcapp.message.dto.request.AddParticipantsRequest;
import com.uit.sociusmvcapp.message.dto.request.CreateGroupConversationRequest;
import com.uit.sociusmvcapp.message.dto.request.ParticipantRequest;
import com.uit.sociusmvcapp.message.dto.request.UpdateConversationRequest;
import com.uit.sociusmvcapp.message.dto.request.UpdateParticipantSettingsRequest;
import com.uit.sociusmvcapp.message.enums.ConversationType;
import com.uit.sociusmvcapp.message.enums.ParticipantRole;
import com.uit.sociusmvcapp.message.internal.adapter.ConversationBlobAdapter;
import com.uit.sociusmvcapp.message.internal.repository.ConversationParticipantRepository;
import com.uit.sociusmvcapp.message.internal.repository.ConversationRepository;
import com.uit.sociusmvcapp.shared.constants.CommonConstant;
import com.uit.sociusmvcapp.shared.constants.MessageConstant;
import com.uit.sociusmvcapp.shared.enums.DeleteFlagEnums;
import com.uit.sociusmvcapp.shared.response.CursorResponse;
import com.uit.sociusmvcapp.shared.service.ExceptionFactory;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

/** Implementation of ConversationService for Conversation-related operations. */
@Service
@Slf4j
@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService {

  /** Maximum number of conversations to fetch for search. */
  private static final int MAX_SEARCH_FETCH_LIMIT = 1000;

  private final ConversationRepository conversationRepository;
  private final ConversationParticipantRepository participantRepository;
  private final UserContentProvider userContentProvider;
  private final ConversationBlobAdapter conversationBlobAdapter;
  private final EmployeeService employeeService;

  /**
   * Get or create a direct conversation between current user and target user. Implements lazy
   * creation pattern - returns existing conversation if it exists, or creates a new one.
   *
   * @param targetEmployeeId the target employee ID
   * @return the conversation DTO (existing or newly created)
   */
  @Override
  @Transactional
  public ConversationDto getOrCreateDirectConversation(String targetEmployeeId) {
    String currentClientId = getCurrentClientId();

    if (currentClientId.equals(targetEmployeeId)) {
      throw ExceptionFactory.badRequest(MessageConstant.E_MSG_011);
    }

    ConversationDto existingConversation =
        conversationRepository.findDirectConversationBetweenUsers(
            currentClientId, targetEmployeeId);

    if (existingConversation != null) {
      // Set the other participant's name and avatar for DIRECT conversations
      enrichDirectConversation(existingConversation, currentClientId);
      return existingConversation;
    }

    String conversationId = UUID.randomUUID().toString();

    ConversationDto conversation =
        ConversationDto.builder()
            .conversationId(conversationId)
            .type(ConversationType.DIRECT.getCode())
            .createdBy(currentClientId)
            .build();

    conversationRepository.insert(conversation);

    List<ConversationParticipantDto> participants = new ArrayList<>();

    participants.add(
        buildParticipant(conversationId, currentClientId, ParticipantRole.MEMBER.getCode()));
    participants.add(
        buildParticipant(conversationId, targetEmployeeId, ParticipantRole.MEMBER.getCode()));

    participantRepository.insertBatch(participants);

    ConversationDto result = conversationRepository.findByConversationId(conversationId);
    // Set the other participant's name and avatar for the new DIRECT conversation
    enrichDirectConversation(result, currentClientId);
    return result;
  }

  /**
   * Create a new group conversation explicitly.
   *
   * @param request the request containing group conversation creation details
   * @return the created conversation DTO
   */
  @Override
  @Transactional
  public ConversationDto createGroupConversation(CreateGroupConversationRequest request) {
    String currentClientId = getCurrentClientId();

    String conversationId = UUID.randomUUID().toString();

    ConversationDto conversation =
        ConversationDto.builder()
            .conversationId(conversationId)
            .type(ConversationType.GROUP.getCode())
            .name(request.getName())
            .avatarUrl(request.getAvatarUrl())
            .createdBy(currentClientId)
            .build();

    conversationRepository.insert(conversation);

    List<ConversationParticipantDto> participants = new ArrayList<>();
    participants.add(
        buildParticipant(conversationId, currentClientId, ParticipantRole.OWNER.getCode()));

    for (String participantId : request.getParticipantIds()) {
      if (!participantId.equals(currentClientId)) {
        participants.add(
            buildParticipant(conversationId, participantId, ParticipantRole.MEMBER.getCode()));
      }
    }

    participantRepository.insertBatch(participants);

    return conversationRepository.findByConversationId(conversationId);
  }

  /**
   * Get a conversation by its unique ID.
   *
   * @param conversationId the unique conversation ID
   * @return the conversation DTO
   */
  @Override
  public ConversationDto getByConversationId(String conversationId) {
    validateParticipant(conversationId, getCurrentClientId());

    ConversationDto conversation = conversationRepository.findByConversationId(conversationId);
    if (conversation == null) {
      throw ExceptionFactory.notFound(MessageConstant.E_MSG_002);
    }
    return conversation;
  }

  /**
   * Update a conversation.
   *
   * @param conversationId the unique conversation ID
   * @param request the request containing update details
   * @return the updated conversation DTO
   */
  @Override
  @Transactional
  public ConversationDto update(String conversationId, UpdateConversationRequest request) {

    validateParticipant(conversationId, getCurrentClientId());

    ConversationDto existingConversation =
        conversationRepository.findByConversationId(conversationId);
    if (existingConversation == null) {
      throw ExceptionFactory.notFound(MessageConstant.E_MSG_002);
    }

    conversationRepository.update(conversationId, request);

    return conversationRepository.findByConversationId(conversationId);
  }

  /**
   * Delete a conversation.
   *
   * @param conversationId the unique conversation ID
   */
  @Override
  @Transactional
  public void delete(String conversationId) {
    ConversationDto conversation = conversationRepository.findByConversationId(conversationId);
    if (conversation == null) {
      throw ExceptionFactory.notFound(MessageConstant.E_MSG_002);
    }

    if (!conversation.getCreatedBy().equals(getCurrentClientId())) {
      throw ExceptionFactory.badRequest(MessageConstant.E_MSG_003);
    }

    conversationRepository.softDelete(conversationId);
  }

  /**
   * Get all conversations for the current user with cursor-based pagination.
   *
   * @param cursor the cursor for pagination (null for first page)
   * @param limit the number of items to retrieve
   * @return cursor response containing conversations
   */
  @Override
  public CursorResponse<ConversationDto> getConversations(String cursor, int limit) {

    String currentClientId = getCurrentClientId();
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
      } catch (IllegalArgumentException | DateTimeParseException e) {
        log.warn("Invalid cursor format, ignoring cursor: {}", cursor);
        // Continue without cursor - will return first page
      }
    }

    List<ConversationDto> conversations =
        conversationRepository.findByEmployeeIdWithCursor(
            currentClientId, lastMessageAt, lastId, limit);

    // Enrich DIRECT conversations with the other participant's name and avatar
    enrichDirectConversations(conversations, currentClientId);

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

  /**
   * Get all participants of a conversation.
   *
   * @param conversationId the conversation ID
   * @return list of participant DTOs
   */
  @Override
  public List<ConversationParticipantDto> getParticipants(String conversationId) {

    validateParticipant(conversationId, getCurrentClientId());

    return participantRepository.findByConversationId(conversationId);
  }

  /**
   * Add a participant to a conversation (group only).
   *
   * @param conversationId the conversation ID
   * @param request the request containing participant details
   * @return the added participant DTO
   */
  @Override
  @Transactional
  public List<ConversationParticipantDto> addParticipant(
      String conversationId, AddParticipantsRequest request) {

    validateParticipant(conversationId, getCurrentClientId());
    validateDirectConversationType(conversationId);

    List<ParticipantRequest> participants = normalizeParticipants(request);
    Set<String> employeeIds = extractAndValidateEmployeeIds(participants);

    Map<String, ConversationParticipantDto> existingMap =
        loadExistingParticipants(conversationId, employeeIds);

    ParticipantBatch batch = classifyParticipants(conversationId, participants, existingMap);

    persistBatch(conversationId, batch);
    return participantRepository.findAllByConversationIdAndEmployeeIds(conversationId, employeeIds);
  }

  /**
   * Remove a participant from a conversation (group only).
   *
   * @param conversationId the conversation ID
   * @param employeeIds the list of employee IDs to remove
   */
  @Override
  @Transactional
  public void removeParticipant(String conversationId, List<String> employeeIds) {

    validateParticipant(conversationId, getCurrentClientId());

    ConversationDto conversation = conversationRepository.findByConversationId(conversationId);
    if (conversation != null && employeeIds.contains(conversation.getCreatedBy())) {
      throw ExceptionFactory.badRequest(MessageConstant.E_MSG_005);
    }

    participantRepository.softDeleteBatch(conversationId, employeeIds);
  }

  /**
   * Current user leaves a conversation.
   *
   * @param conversationId the conversation ID
   */
  @Override
  @Transactional
  public void leaveConversation(String conversationId) {
    String currentClientId = getCurrentClientId();

    validateParticipant(conversationId, currentClientId);

    ConversationDto conversation = conversationRepository.findByConversationId(conversationId);
    if (conversation != null && conversation.getCreatedBy().equals(currentClientId)) {
      throw ExceptionFactory.badRequest(MessageConstant.E_MSG_006);
    }
    participantRepository.softDelete(conversationId, currentClientId);
  }

  /**
   * Update participant settings (mute, pin) for the current user in a conversation.
   *
   * @param conversationId the conversation ID
   * @param request the request containing settings to update
   */
  @Override
  @Transactional
  public void updateParticipantSettings(
      String conversationId, UpdateParticipantSettingsRequest request) {
    String currentClientId = getCurrentClientId();

    // Verify user is a participant
    validateParticipant(conversationId, currentClientId);

    participantRepository.updateSettings(
        conversationId, currentClientId, request.getIsMuted(), request.getIsPinned());
  }

  /**
   * Mark messages as read up to the specified message ID in a conversation.
   *
   * @param conversationId the conversation ID
   * @param messageId the message ID up to which messages are marked as read
   */
  @Override
  @Transactional
  public void markAsRead(String conversationId, String messageId) {
    String currentClientId = getCurrentClientId();

    // Verify user is a participant
    validateParticipant(conversationId, currentClientId);

    participantRepository.updateLastReadMessage(conversationId, currentClientId, messageId);
  }

  /**
   * Upload a file to the conversation.
   *
   * @param conversationId the conversation ID
   * @param file the file to upload
   * @return metadata of the uploaded file
   */
  @Override
  public UploadFileDto uploadFile(String conversationId, MultipartFile file) {
    return conversationBlobAdapter.uploadAvatar(file, conversationId);
  }

  /**
   * Validates that the given employee is a participant in the specified conversation.
   *
   * @param conversationId the conversation ID
   * @param employeeId the employee ID
   */
  private void validateParticipant(String conversationId, String employeeId) {
    Boolean isParticipant =
        participantRepository.existsByConversationIdAndEmployeeId(conversationId, employeeId);
    if (!Boolean.TRUE.equals(isParticipant)) {
      throw ExceptionFactory.badRequest(MessageConstant.E_MSG_007);
    }
  }

  /**
   * Builds a ConversationParticipant entity.
   *
   * @param conversationId the conversation ID
   * @param employeeId the employee ID
   * @param role the participant role
   * @return the ConversationParticipant entity
   */
  private ConversationParticipantDto buildParticipant(
      String conversationId, String employeeId, String role) {
    return ConversationParticipantDto.builder()
        .conversationId(conversationId)
        .employeeId(employeeId)
        .role(role)
        .build();
  }

  /**
   * Validates that the conversation is not of DIRECT type.
   *
   * @param conversationId the conversation ID
   */
  private void validateDirectConversationType(String conversationId) {
    ConversationDto conversation = conversationRepository.findByConversationId(conversationId);

    if (conversation != null && ConversationType.DIRECT.getCode().equals(conversation.getType())) {
      throw ExceptionFactory.badRequest(MessageConstant.E_MSG_012);
    }
  }

  /**
   * Normalizes the participants from the request, ensuring a non-null list.
   *
   * @param request the AddParticipantsRequest containing participant details
   * @return the normalized list of ParticipantRequest
   */
  private List<ParticipantRequest> normalizeParticipants(AddParticipantsRequest request) {

    if (CollectionUtils.isEmpty(request.getParticipants())) {
      return List.of();
    }

    return request.getParticipants();
  }

  /**
   * Extracts and validates employee IDs from the participant requests, ensuring no duplicates.
   *
   * @param participants the list of ParticipantRequest
   * @return the set of unique employee IDs
   */
  private Set<String> extractAndValidateEmployeeIds(List<ParticipantRequest> participants) {
    Set<String> employeeIds = new HashSet<>();
    for (ParticipantRequest p : participants) {
      if (!employeeIds.add(p.getEmployeeId())) {
        throw ExceptionFactory.badRequest(MessageConstant.E_MSG_013);
      }
    }
    return employeeIds;
  }

  /**
   * Loads existing participants for a conversation given a set of employee IDs.
   *
   * @param conversationId the conversation ID
   * @param employeeIds the set of employee IDs
   * @return a map of existing participants by employee ID
   */
  private Map<String, ConversationParticipantDto> loadExistingParticipants(
      String conversationId, Set<String> employeeIds) {

    return participantRepository
        .findAllIncludingDeletedByConversationIdAndEmployeeIds(conversationId, employeeIds)
        .stream()
        .collect(Collectors.toMap(ConversationParticipantDto::getEmployeeId, Function.identity()));
  }

  /**
   * Classifies participants into those to insert and those to revive.
   *
   * @param toInsert the list of participants to insert
   * @param toRevive the list of employee IDs to revive
   */
  private record ParticipantBatch(
      List<ConversationParticipantDto> toInsert, List<String> toRevive) {}

  /**
   * Classifies participants into those to insert and those to revive.
   *
   * @param conversationId the conversation ID
   * @param participants the list of ParticipantRequest
   * @param existingMap the map of existing participants by employee ID
   * @return the ParticipantBatch containing lists of participants to insert and revive
   */
  private ParticipantBatch classifyParticipants(
      String conversationId,
      List<ParticipantRequest> participants,
      Map<String, ConversationParticipantDto> existingMap) {

    List<ConversationParticipantDto> toInsert = new ArrayList<>();
    List<String> toRevive = new ArrayList<>();

    for (ParticipantRequest participant : participants) {
      classifySingleParticipant(conversationId, participant, existingMap, toInsert, toRevive);
    }

    return new ParticipantBatch(toInsert, toRevive);
  }

  /**
   * Persists the classified batch of participants by inserting new ones and reviving deleted ones.
   *
   * @param conversationId the conversation ID
   * @param request the ParticipantRequest
   * @param existingMap the map of existing participants by employee ID
   * @param toInsert the list of participants to insert
   * @param toRevive the list of employee IDs to revive
   */
  private void classifySingleParticipant(
      String conversationId,
      ParticipantRequest request,
      Map<String, ConversationParticipantDto> existingMap,
      List<ConversationParticipantDto> toInsert,
      List<String> toRevive) {

    String employeeId = request.getEmployeeId();
    ConversationParticipantDto existing = existingMap.get(employeeId);

    if (existing != null
        && DeleteFlagEnums.NOT_DELETED.getValue().equals(existing.getDeleteFlag())) {
      throw ExceptionFactory.badRequest(MessageConstant.E_MSG_004);
    }

    if (existing != null && DeleteFlagEnums.DELETED.getValue().equals(existing.getDeleteFlag())) {
      toRevive.add(employeeId);
      return;
    }
    String role = request.getRole() != null ? request.getRole() : ParticipantRole.MEMBER.getCode();
    toInsert.add(buildParticipant(conversationId, employeeId, role));
  }

  /**
   * Persists the classified batch of participants by inserting new ones and reviving deleted ones.
   *
   * @param conversationId the conversation ID
   * @param batch the ParticipantBatch containing lists of participants to insert and revive
   */
  private void persistBatch(String conversationId, ParticipantBatch batch) {

    if (!batch.toRevive().isEmpty()) {
      participantRepository.reviveParticipantBatch(conversationId, batch.toRevive());
    }

    if (!batch.toInsert().isEmpty()) {
      participantRepository.insertBatch(batch.toInsert());
    }
  }

  /**
   * Get the current client ID from user content.
   *
   * @return the current client ID
   */
  private String getCurrentClientId() {
    return userContentProvider.getUserContent().getClientId();
  }

  /**
   * Enrich a list of DIRECT conversations with the other participant's name and avatar.
   *
   * @param conversations the list of conversations to enrich
   * @param currentClientId the current user's client ID
   */
  private void enrichDirectConversations(
      List<ConversationDto> conversations, String currentClientId) {
    if (conversations == null || conversations.isEmpty()) {
      return;
    }

    // Filter DIRECT conversations that need enrichment
    List<ConversationDto> directConversations =
        conversations.stream()
            .filter(c -> ConversationType.DIRECT.getCode().equals(c.getType()))
            .toList();

    if (directConversations.isEmpty()) {
      return;
    }

    // Collect all conversation IDs to fetch participants
    for (ConversationDto conversation : directConversations) {
      enrichDirectConversation(conversation, currentClientId);
    }
  }

  /**
   * Enrich a single DIRECT conversation with the other participant's name and avatar.
   *
   * @param conversation the conversation to enrich
   * @param currentClientId the current user's client ID
   */
  private void enrichDirectConversation(ConversationDto conversation, String currentClientId) {
    if (conversation == null || !ConversationType.DIRECT.getCode().equals(conversation.getType())) {
      return;
    }

    // Get participants and find the other participant
    List<ConversationParticipantDto> participants =
        participantRepository.findByConversationId(conversation.getConversationId());

    String otherParticipantId =
        participants.stream()
            .map(ConversationParticipantDto::getEmployeeId)
            .filter(id -> !id.equals(currentClientId))
            .findFirst()
            .orElse(null);

    if (otherParticipantId != null) {
      try {
        EmployeeDto otherEmployee = employeeService.findByClientId(otherParticipantId);
        if (otherEmployee != null) {
          String displayName = buildDisplayName(otherEmployee);
          conversation.setName(displayName);
          conversation.setAvatarUrl(otherEmployee.getImageUrl());
        }
      } catch (RuntimeException e) {
        log.warn(
            "Failed to fetch employee info for DIRECT conversation enrichment: {}", e.getMessage());
      }
    }
  }

  /**
   * Build display name from employee data.
   *
   * @param employee the employee DTO
   * @return the display name (firstName + lastName)
   */
  private String buildDisplayName(EmployeeDto employee) {
    StringBuilder name = new StringBuilder();
    if (employee.getFirstName() != null) {
      name.append(employee.getFirstName());
    }
    if (employee.getLastName() != null) {
      if (name.length() > 0) {
        name.append(" ");
      }
      name.append(employee.getLastName());
    }
    return name.toString();
  }

  /**
   * Search conversations by keyword. For GROUP conversations, searches by conversation name. For
   * DIRECT conversations, searches by the other participant's name.
   *
   * @param keyword the search keyword
   * @param limit the maximum number of results
   * @return list of matching conversations
   */
  @Override
  public List<ConversationDto> searchConversations(String keyword, int limit) {
    if (keyword == null || keyword.trim().isEmpty()) {
      return List.of();
    }

    String currentClientId = getCurrentClientId();
    String searchKeyword = keyword.trim().toLowerCase();

    // Get all user's conversations (limited to avoid performance issues)
    List<ConversationDto> allConversations =
        conversationRepository.findByEmployeeIdWithCursor(
            currentClientId, null, null, MAX_SEARCH_FETCH_LIMIT);

    // Enrich DIRECT conversations with participant info
    enrichDirectConversations(allConversations, currentClientId);

    // Filter conversations matching the search keyword
    return allConversations.stream()
        .filter(
            c -> {
              String name = c.getName();
              return name != null && name.toLowerCase().contains(searchKeyword);
            })
        .limit(limit)
        .toList();
  }
}
