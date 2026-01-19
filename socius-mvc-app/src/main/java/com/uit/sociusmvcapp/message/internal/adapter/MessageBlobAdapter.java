package com.uit.sociusmvcapp.message.internal.adapter;

import com.uit.sociusmvcapp.azure.blob.AzureBlobProperties;
import com.uit.sociusmvcapp.azure.blob.AzureBlobService;
import com.uit.sociusmvcapp.azure.blob.UploadRequest;
import com.uit.sociusmvcapp.message.dto.FileMetadataDto;
import com.uit.sociusmvcapp.shared.constants.MessageConstant;
import com.uit.sociusmvcapp.shared.service.ExceptionFactory;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * Adapter for Azure Blob Storage operations specific to Message file uploads. This class hides
 * Azure Blob configuration details from the service layer and uses a separate container for message
 * files.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MessageBlobAdapter {

  /** Azure Blob Service for file operations. */
  private final AzureBlobService azureBlobService;

  /** Apache Tika for MIME type detection. */
  private final Tika tika = new Tika();

  /** Azure Blob Storage connection string. */
  @Value("${azure.blob.connection-string}")
  private String connectionString;

  /** Azure Blob Storage container name for message files. */
  @Value("${azure.blob.message-file.container-name}")
  private String containerName;

  /**
   * Upload a file for a message and return file metadata.
   *
   * @param file the file to upload
   * @param conversationId the conversation ID for organizing files
   * @return FileMetadataDto containing file metadata
   */
  public FileMetadataDto uploadMessageFile(MultipartFile file, String conversationId) {
    String mimeType = detectMimeType(file);
    UploadRequest request = buildUploadRequest(file, conversationId, mimeType);
    if (request == null) {
      log.error("Failed to build upload request for conversationId: {}", conversationId);
      throw ExceptionFactory.internalError(MessageConstant.E_SYS_004);
    }

    AzureBlobProperties properties = buildBlobProperties();
    String filePath = azureBlobService.uploadFile(properties, request);

    return FileMetadataDto.builder()
        .fileName(file.getOriginalFilename())
        .filePath(filePath)
        .fileSize(file.getSize())
        .mimeType(mimeType)
        .build();
  }

  /**
   * Upload multiple files for a message and return file metadata list. All files are stored in the
   * same timestamp folder.
   *
   * @param files the list of files to upload
   * @param conversationId the conversation ID for organizing files
   * @return list of FileMetadataDto containing file metadata
   */
  public List<FileMetadataDto> uploadMessageFiles(
      List<MultipartFile> files, String conversationId) {
    List<UploadRequest> uploadRequests = new java.util.ArrayList<>();
    List<String> mimeTypes = new java.util.ArrayList<>();
    List<Long> fileSizes = new java.util.ArrayList<>();
    List<String> fileNames = new java.util.ArrayList<>();

    for (MultipartFile file : files) {
      String mimeType = detectMimeType(file);
      mimeTypes.add(mimeType);
      fileSizes.add(file.getSize());
      fileNames.add(file.getOriginalFilename());

      UploadRequest request = buildUploadRequest(file, conversationId, mimeType);
      if (request == null) {
        log.error("Failed to build upload request for conversationId: {}", conversationId);
        throw ExceptionFactory.internalError(MessageConstant.E_SYS_004);
      }
      uploadRequests.add(request);
    }

    AzureBlobProperties properties = buildBlobProperties();
    List<String> filePaths =
        azureBlobService.uploadFiles(properties, uploadRequests, conversationId);

    List<FileMetadataDto> metadataList = new java.util.ArrayList<>();
    for (int i = 0; i < filePaths.size(); i++) {
      metadataList.add(
          FileMetadataDto.builder()
              .fileName(fileNames.get(i))
              .filePath(filePaths.get(i))
              .fileSize(fileSizes.get(i))
              .mimeType(mimeTypes.get(i))
              .build());
    }

    return metadataList;
  }

  /**
   * Download a single file to the output stream.
   *
   * @param filePath the file path in blob storage
   * @param outputStream the output stream to write file content
   */
  public void downloadFile(String filePath, OutputStream outputStream) {
    AzureBlobProperties properties = buildBlobProperties();
    azureBlobService.downloadFile(properties, filePath, outputStream);
  }

  /**
   * Download multiple files as a ZIP archive.
   *
   * @param filePaths the list of file paths to download
   * @param outputStream the output stream to write ZIP content
   */
  public void downloadFilesAsZip(List<String> filePaths, OutputStream outputStream) {
    AzureBlobProperties properties = buildBlobProperties();
    azureBlobService.downloadFilesAsZip(properties, filePaths, outputStream);
  }

  /**
   * Get the original file name from the file path.
   *
   * @param filePath the file path in blob storage
   * @return the original file name
   */
  public String getOriginalFileName(String filePath) {
    return azureBlobService.getOriginalFileName(filePath);
  }

  /**
   * Check if a file exists in blob storage.
   *
   * @param filePath the file path to check
   * @return true if file exists, false otherwise
   */
  public boolean fileExists(String filePath) {
    AzureBlobProperties properties = buildBlobProperties();
    return azureBlobService.fileExists(properties, filePath);
  }

  /**
   * Delete a file from blob storage.
   *
   * @param filePath the file path to delete
   */
  public void deleteFile(String filePath) {
    AzureBlobProperties properties = buildBlobProperties();
    azureBlobService.deleteFile(properties, filePath);
  }

  /**
   * Detect MIME type of the file using Apache Tika.
   *
   * @param file the file to detect
   * @return the detected MIME type
   */
  private String detectMimeType(MultipartFile file) {
    try (InputStream inputStream = file.getInputStream()) {
      return tika.detect(inputStream, file.getOriginalFilename());
    } catch (Exception e) {
      log.warn("Failed to detect MIME type, using default: {}", e.getMessage());
      return "application/octet-stream";
    }
  }

  /** Build an UploadRequest from a MultipartFile, conversationId, and mimeType. */
  private UploadRequest buildUploadRequest(
      MultipartFile file, String conversationId, String mimeType) {
    try {
      return UploadRequest.builder()
          .clientId(conversationId)
          .fileName(file.getOriginalFilename())
          .contentType(mimeType)
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
