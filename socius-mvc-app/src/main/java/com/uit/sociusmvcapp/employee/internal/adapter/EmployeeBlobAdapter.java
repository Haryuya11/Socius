package com.uit.sociusmvcapp.employee.internal.adapter;

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
 * Adapter for Azure Blob Storage operations specific to Employee module. This class hides Azure
 * Blob configuration details from the service layer.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EmployeeBlobAdapter {

  /** Azure Blob Service for file operations. */
  private final AzureBlobService azureBlobService;

  /** Azure Blob Storage connection string. */
  @Value("${azure.blob.connection-string}")
  private String connectionString;

  /** Azure Blob Storage container name for user. */
  @Value("${azure.blob.user.container-name}")
  private String userContainerName;

  /**
   * Upload an avatar file for an employee.
   *
   * @param file the avatar file to upload
   * @param clientId the employee's client ID
   * @return UploadFileDto containing upload details
   */
  public UploadFileDto uploadAvatar(MultipartFile file, String clientId) {
    UploadRequest request = buildUploadRequest(file, clientId);
    if (request == null) {
      log.error("Failed to build upload request for clientId: {}", clientId);
      throw ExceptionFactory.internalError(MessageConstant.E_SYS_004);
    }

    AzureBlobProperties properties = buildUserBlobProperties();
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
    AzureBlobProperties properties = buildUserBlobProperties();
    return azureBlobService.generateSasToken(properties, path);
  }

  /**
   * Delete an avatar file.
   *
   * @param path the avatar path
   */
  public void deleteAvatar(String path) {
    AzureBlobProperties properties = buildUserBlobProperties();
    azureBlobService.deleteFile(properties, path);
  }

  /**
   * Check if an avatar exists.
   *
   * @param path the avatar path
   * @return true if the avatar exists, false otherwise
   */
  public boolean avatarExists(String path) {
    AzureBlobProperties properties = buildUserBlobProperties();
    return azureBlobService.fileExists(properties, path);
  }

  /** Build an UploadRequest from a MultipartFile and clientId. */
  private UploadRequest buildUploadRequest(MultipartFile file, String clientId) {
    try {
      return UploadRequest.builder()
          .clientId(clientId)
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
   * Build AzureBlobProperties for user avatar operations.
   *
   * @return AzureBlobProperties instance
   */
  private AzureBlobProperties buildUserBlobProperties() {
    return AzureBlobProperties.builder()
        .connectionString(connectionString)
        .containerName(userContainerName)
        .build();
  }
}
