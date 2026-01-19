package com.uit.sociusmvcapp.message;

import com.uit.sociusmvcapp.message.dto.FileMetadataDto;
import com.uit.sociusmvcapp.message.dto.MessageDto;
import com.uit.sociusmvcapp.message.dto.MessageReactionDto;
import com.uit.sociusmvcapp.message.dto.request.FileDownloadRequest;
import com.uit.sociusmvcapp.message.dto.request.MessageReactionRequest;
import com.uit.sociusmvcapp.message.dto.request.SendMessageRequest;
import com.uit.sociusmvcapp.message.dto.request.UpdateMessageRequest;
import com.uit.sociusmvcapp.shared.response.CursorResponse;
import java.io.OutputStream;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

/** Service interface for Message-related operations. */
public interface MessageService {

  /**
   * Send a new message.
   *
   * @param request the request containing message details
   * @return the sent message DTO
   */
  MessageDto sendMessage(SendMessageRequest request);

  /**
   * Get a message by its unique ID.
   *
   * @param messageId the unique message ID
   * @return the message DTO
   */
  MessageDto getByMessageId(String messageId);

  /**
   * Get messages in a conversation with cursor-based pagination.
   *
   * @param conversationId the conversation ID
   * @param cursor the pagination cursor
   * @param limit the number of items to retrieve
   * @return the cursor response containing messages
   */
  CursorResponse<MessageDto> getMessages(String conversationId, String cursor, int limit);

  /**
   * Update a message.
   *
   * @param messageId the message ID
   * @param request the request containing update details
   * @return the updated message DTO
   */
  MessageDto updateMessage(String messageId, UpdateMessageRequest request);

  /**
   * Delete a message.
   *
   * @param messageId the message ID
   */
  void deleteMessage(String messageId);

  /**
   * Add a reaction to a message.
   *
   * @param request the reaction request
   * @return the added reaction DTO
   */
  MessageReactionDto addReaction(MessageReactionRequest request);

  /**
   * Remove a reaction from a message.
   *
   * @param request the reaction request
   */
  void removeReaction(MessageReactionRequest request);

  /**
   * Get all reactions for a message.
   *
   * @param messageId the message ID
   * @return list of reaction DTOs
   */
  List<MessageReactionDto> getReactions(String messageId);

  /**
   * Upload files for a message in a conversation.
   *
   * @param conversationId the conversation ID
   * @param files the files to upload
   * @return list of file metadata DTOs
   */
  List<FileMetadataDto> uploadMessageFiles(String conversationId, List<MultipartFile> files);

  /**
   * Download a single file from a message.
   *
   * @param conversationId the conversation ID for access validation
   * @param request the file download request containing file path
   * @param outputStream the output stream to write file content
   */
  void downloadFile(String conversationId, FileDownloadRequest request, OutputStream outputStream);

  /**
   * Download multiple files as a ZIP archive.
   *
   * @param conversationId the conversation ID for access validation
   * @param requests the list of file download requests
   * @param outputStream the output stream to write ZIP content
   */
  void downloadFilesAsZip(
      String conversationId, List<FileDownloadRequest> requests, OutputStream outputStream);

  /**
   * Get the original file name from a file download request.
   *
   * @param request the file download request
   * @return the original file name
   */
  String getOriginalFileName(FileDownloadRequest request);

  /**
   * Get the content type (MIME type) of a file from a file download request.
   *
   * @param request the file download request
   * @return the content type of the file
   */
  String getContentType(FileDownloadRequest request);
}
