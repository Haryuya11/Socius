package com.uit.sociusmvcapp.message;

import com.uit.sociusmvcapp.azure.blob.UploadFileDto;
import com.uit.sociusmvcapp.message.dto.ConversationDto;
import com.uit.sociusmvcapp.message.dto.ConversationParticipantDto;
import com.uit.sociusmvcapp.message.dto.request.AddParticipantsRequest;
import com.uit.sociusmvcapp.message.dto.request.CreateGroupConversationRequest;
import com.uit.sociusmvcapp.message.dto.request.UpdateConversationRequest;
import com.uit.sociusmvcapp.message.dto.request.UpdateParticipantSettingsRequest;
import com.uit.sociusmvcapp.shared.response.CursorResponse;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

/** Service interface for Conversation-related operations. */
public interface ConversationService {

  /**
   * Get or create a direct conversation between current user and target user. Implements lazy
   * creation pattern - returns existing conversation if it exists, or creates a new one.
   *
   * @param targetEmployeeId the target employee ID
   * @return the conversation DTO (existing or newly created)
   */
  ConversationDto getOrCreateDirectConversation(String targetEmployeeId);

  /**
   * Create a new group conversation explicitly.
   *
   * @param request the request containing group conversation creation details
   * @return the created conversation DTO
   */
  ConversationDto createGroupConversation(CreateGroupConversationRequest request);

  /**
   * Get a conversation by its unique ID.
   *
   * @param conversationId the unique conversation ID
   * @return the conversation DTO
   */
  ConversationDto getByConversationId(String conversationId);

  /**
   * Update a conversation.
   *
   * @param conversationId the unique conversation ID
   * @param request the request containing update details
   * @return the updated conversation DTO
   */
  ConversationDto update(String conversationId, UpdateConversationRequest request);

  /**
   * Delete a conversation.
   *
   * @param conversationId the unique conversation ID
   */
  void delete(String conversationId);

  /**
   * Get all conversations for the current user with cursor-based pagination.
   *
   * @param cursor the cursor for pagination (null for first page)
   * @param limit the number of items to retrieve
   * @return cursor response containing conversations
   */
  CursorResponse<ConversationDto> getConversations(String cursor, int limit);

  /**
   * Get all participants of a conversation.
   *
   * @param conversationId the conversation ID
   * @return list of participant DTOs
   */
  List<ConversationParticipantDto> getParticipants(String conversationId);

  /**
   * Add a participant to a conversation (group only).
   *
   * @param conversationId the conversation ID
   * @param request the request containing participant details
   * @return the added participant DTO
   */
  List<ConversationParticipantDto> addParticipant(
      String conversationId, AddParticipantsRequest request);

  /**
   * Remove a participant from a conversation (group only).
   *
   * @param conversationId the conversation ID
   * @param employeeIds the list of employee IDs to remove
   */
  void removeParticipant(String conversationId, List<String> employeeIds);

  /**
   * Leave a conversation (current user).
   *
   * @param conversationId the conversation ID
   */
  void leaveConversation(String conversationId);

  /**
   * Update participant settings (mute, pin).
   *
   * @param conversationId the conversation ID
   * @param request the request containing settings
   */
  void updateParticipantSettings(String conversationId, UpdateParticipantSettingsRequest request);

  /**
   * Mark messages as read in a conversation.
   *
   * @param conversationId the conversation ID
   * @param messageId the last read message ID
   */
  void markAsRead(String conversationId, String messageId);

  /**
   * Upload a file to a conversation.
   *
   * @param conversationId the conversation ID
   * @param file the file to upload
   * @return metadata of the uploaded file
   */
  UploadFileDto uploadFile(String conversationId, MultipartFile file);
}
