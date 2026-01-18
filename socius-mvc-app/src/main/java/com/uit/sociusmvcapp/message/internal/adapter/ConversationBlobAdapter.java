package com.uit.sociusmvcapp.message.internal.adapter;

import com.uit.sociusmvcapp.azure.blob.AzureBlobProperties;
import com.uit.sociusmvcapp.azure.blob.AzureBlobService;
import com.uit.sociusmvcapp.azure.blob.UploadFileDto;
import com.uit.sociusmvcapp.azure.blob.UploadRequest;
import com.uit.sociusmvcapp.shared.constants.MessageConstant;
import com.uit.sociusmvcapp.shared.service.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * Adapter for Azure Blob Storage operations specific to Conversation module. This class hides Azure
 * Blob configuration details from the service layer.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ConversationBlobAdapter {

  /** Azure Blob Service for file operations. */
  private final AzureBlobService azureBlobService;

  /** Azure Blob Storage connection string. */
  @Value("${azure.blob.connection-string}")
  private String connectionString;

  /** Azure Blob Storage container name for user. */
  @Value("${azure.blob.conversation.container-name}")
  private String containerName;

  /**
   * Upload an avatar file for an employee.
   *
   * @param file the avatar file to upload
   * @param coversationId the conversation's client ID
   * @return UploadFileDto containing upload details
   */
  public UploadFileDto uploadAvatar(MultipartFile file, String coversationId) {
    UploadRequest request = buildUploadRequest(file, coversationId);
    if (request == null) {
      log.error("Failed to build upload request for conversationId: {}", coversationId);
      throw ExceptionFactory.internalError(MessageConstant.E_SYS_004);
    }

    AzureBlobProperties properties = buildBlobProperties();
    String path = azureBlobService.uploadFile(properties, request);
    String sasToken = azureBlobService.generateSasToken(properties, path);

    return UploadFileDto.builder().path(path).url(sasToken).build();
  }

  /**
   * Get the URL for an avatar.
   *
   * @param path the avatar path
   * @return the avatar URL with SAS token
   */
  public String getAvatarUrl(String path) {
    AzureBlobProperties properties = buildBlobProperties();
    return azureBlobService.generateSasToken(properties, path);
  }

  /**
   * Delete an avatar file.
   *
   * @param path the avatar path
   */
  public void deleteAvatar(String path) {
    AzureBlobProperties properties = buildBlobProperties();
    azureBlobService.deleteFile(properties, path);
  }

  /**
   * Check if an avatar exists.
   *
   * @param path the avatar path
   * @return true if the avatar exists, false otherwise
   */
  public boolean avatarExists(String path) {
    AzureBlobProperties properties = buildBlobProperties();
    return azureBlobService.fileExists(properties, path);
  }

  /** Build an UploadRequest from a MultipartFile and clientId. */
  private UploadRequest buildUploadRequest(MultipartFile file, String conversationId) {
    try {
      return UploadRequest.builder()
          .clientId(conversationId)
          .fileName(file.getOriginalFilename())
          .contentType(file.getContentType())
          .fileSize(file.getSize())
          .inputStream(file.getInputStream())
          .build();
    } catch (Exception e) {
      log.error("Failed to build upload request: {}", e.getMessage(), e);
      return null;
    }
  }

  /**
   * Build AzureBlobProperties for file operations.
   *
   * @return AzureBlobProperties instance
   */
  private AzureBlobProperties buildBlobProperties() {
    return AzureBlobProperties.builder()
        .connectionString(connectionString)
        .containerName(containerName)
        .build();
  }
}
