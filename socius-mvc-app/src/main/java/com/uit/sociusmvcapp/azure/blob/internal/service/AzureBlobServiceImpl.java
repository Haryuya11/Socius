package com.uit.sociusmvcapp.azure.blob.internal.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.azure.storage.blob.options.BlobParallelUploadOptions;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import com.uit.sociusmvcapp.azure.blob.AzureBlobProperties;
import com.uit.sociusmvcapp.azure.blob.AzureBlobService;
import com.uit.sociusmvcapp.azure.blob.UploadRequest;
import com.uit.sociusmvcapp.azure.blob.internal.domain.BlobClientProperties;
import com.uit.sociusmvcapp.azure.blob.internal.factory.BlobClientFactory;
import com.uit.sociusmvcapp.shared.constants.MessageConstant;
import com.uit.sociusmvcapp.shared.service.ExceptionFactory;
import java.io.IOException;
import java.io.OutputStream;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** Service implementation for interacting with Azure Blob Storage. */
@Slf4j
@Service
@RequiredArgsConstructor
public class AzureBlobServiceImpl implements AzureBlobService {

  /** SAS token expiry time in minutes. */
  private static final Integer SAS_TOKEN_EXPIRY_MINUTES = 120;

  /** BlobClientFactory for creating Blob clients. */
  private final BlobClientFactory blobClientFactory;

  /**
   * Upload a file to Azure Blob Storage.
   *
   * @param properties Azure Blob configuration properties
   * @param request the upload request containing file details
   * @return the path of the uploaded file
   */
  @Override
  public String uploadFile(AzureBlobProperties properties, UploadRequest request) {
    try {
      log.info("Uploading file to container: {}", properties.getContainerName());
      BlobContainerClient containerClient = createBlobContainerClient(properties);

      ensureContainerExists(containerClient, properties.getContainerName());

      String blobName = generateBlobName(request.getClientId(), request.getFileName());
      BlobClient blobClient = containerClient.getBlobClient(blobName);

      uploadToBlob(blobClient, request);
      log.info("File uploaded successfully: {}", blobName);
      return blobName;
    } catch (Exception e) {
      log.error("Failed to upload file: {}", request.getFileName(), e);
      throw ExceptionFactory.internalError(MessageConstant.E_SYS_004);
    }
  }

  /**
   * Generate a SAS token for accessing a blob.
   *
   * @param properties Azure Blob configuration properties
   * @param blobName the name of the blob
   * @return the SAS token URL
   */
  @Override
  public String generateSasToken(AzureBlobProperties properties, String blobName) {
    try {
      BlobContainerClient containerClient = createBlobContainerClient(properties);
      BlobClient blobClient = containerClient.getBlobClient(blobName);
      verifyBlobExists(blobClient, blobName);

      String sasToken = createSasToken(blobClient);
      String fullUrl = buildFullUrl(blobClient, sasToken);

      log.info("Generated SAS token for blob: {}", blobName);
      return fullUrl;

    } catch (Exception e) {
      log.error("Failed to generate SAS token for blob: {}", blobName, e);
      throw ExceptionFactory.internalError(MessageConstant.E_SYS_003);
    }
  }

  /**
   * Delete a blob from Azure Blob Storage.
   *
   * @param properties Azure Blob configuration properties
   * @param blobName the name of the blob to delete
   */
  @Override
  public void deleteFile(AzureBlobProperties properties, String blobName) {
    try {
      log.info("Deleting blob: {} from container: {}", blobName, properties.getContainerName());

      BlobContainerClient containerClient = createBlobContainerClient(properties);
      BlobClient blobClient = containerClient.getBlobClient(blobName);

      verifyBlobExists(blobClient, blobName);

      blobClient.delete();
      log.info("Successfully deleted blob: {}", blobName);
    } catch (Exception e) {
      log.error("Failed to delete blob: {}", e.getMessage(), e);
      throw ExceptionFactory.internalError(MessageConstant.E_SYS_003);
    }
  }

  /**
   * Check if a blob exists.
   *
   * @param properties Azure Blob configuration properties
   * @param blobName the name of the blob
   * @return true if the blob exists, false otherwise
   */
  @Override
  public boolean fileExists(AzureBlobProperties properties, String blobName) {
    try {
      BlobContainerClient containerClient = createBlobContainerClient(properties);
      BlobClient blobClient = containerClient.getBlobClient(blobName);
      return blobClient.exists();
    } catch (Exception e) {
      log.error("Failed to check blob existence: {}", e.getMessage(), e);
      return false;
    }
  }

  /**
   * Download a single file from Azure Blob Storage.
   *
   * @param properties Azure Blob configuration properties
   * @param blobName the name of the blob to download
   * @param outputStream the output stream to write the file content
   */
  @Override
  public void downloadFile(
      AzureBlobProperties properties, String blobName, OutputStream outputStream) {
    try {
      log.info("Downloading blob: {} from container: {}", blobName, properties.getContainerName());

      BlobContainerClient containerClient = createBlobContainerClient(properties);
      BlobClient blobClient = containerClient.getBlobClient(blobName);

      verifyBlobExists(blobClient, blobName);

      blobClient.downloadStream(outputStream);
      log.info("Successfully downloaded blob: {}", blobName);
    } catch (Exception e) {
      log.error("Failed to download blob: {}", e.getMessage(), e);
      throw ExceptionFactory.internalError(MessageConstant.E_SYS_003);
    }
  }

  /**
   * Download multiple files from Azure Blob Storage as a ZIP archive.
   *
   * @param properties Azure Blob configuration properties
   * @param blobNames the list of blob names to download
   * @param outputStream the output stream to write the ZIP content
   */
  @Override
  public void downloadFilesAsZip(
      AzureBlobProperties properties, List<String> blobNames, OutputStream outputStream) {
    try {
      log.info(
          "Downloading {} blobs as ZIP from container: {}",
          blobNames.size(),
          properties.getContainerName());

      BlobContainerClient containerClient = createBlobContainerClient(properties);

      try (ZipOutputStream zipOut = new ZipOutputStream(outputStream)) {
        for (String blobName : blobNames) {
          BlobClient blobClient = containerClient.getBlobClient(blobName);

          if (Boolean.FALSE.equals(blobClient.exists())) {
            log.warn("Blob does not exist, skipping: {}", blobName);
            continue;
          }

          String fileName = getOriginalFileName(blobName);
          ZipEntry zipEntry = new ZipEntry(fileName);
          zipOut.putNextEntry(zipEntry);

          blobClient.downloadStream(zipOut);

          zipOut.closeEntry();
        }
      }

      log.info("Successfully created ZIP archive with {} blobs", blobNames.size());
    } catch (IOException e) {
      log.error("Failed to create ZIP archive: {}", e.getMessage(), e);
      throw ExceptionFactory.internalError(MessageConstant.E_SYS_003);
    }
  }

  /**
   * Get the original file name from a blob path.
   *
   * @param blobName the full blob name/path (format: {id}/{timestamp}/{fileName})
   * @return the original file name
   */
  @Override
  public String getOriginalFileName(String blobName) {
    if (blobName == null || blobName.isEmpty()) {
      return "unknown";
    }
    int lastSlash = blobName.lastIndexOf('/');
    if (lastSlash >= 0 && lastSlash < blobName.length() - 1) {
      return blobName.substring(lastSlash + 1);
    }
    return blobName;
  }

  /**
   * Get the content type (MIME type) of a blob from Azure.
   *
   * @param properties Azure Blob configuration properties
   * @param blobName the name of the blob
   * @return the content type of the blob, or "application/octet-stream" if not available
   */
  @Override
  public String getContentType(AzureBlobProperties properties, String blobName) {
    try {
      log.info("Getting content type for blob: {}", blobName);

      BlobContainerClient containerClient = createBlobContainerClient(properties);
      BlobClient blobClient = containerClient.getBlobClient(blobName);

      verifyBlobExists(blobClient, blobName);

      String contentType = blobClient.getProperties().getContentType();
      if (contentType == null || contentType.isBlank()) {
        return "application/octet-stream";
      }
      return contentType;
    } catch (Exception e) {
      log.error("Failed to get content type for blob: {}", e.getMessage(), e);
      return "application/octet-stream";
    }
  }

  /**
   * Upload multiple files to Azure Blob Storage with a shared timestamp folder.
   *
   * @param properties Azure Blob configuration properties
   * @param requests the list of upload requests containing file details
   * @param clientId the client ID (conversation ID) for organizing files
   * @return the list of file paths for uploaded files
   */
  @Override
  public List<String> uploadFiles(
      AzureBlobProperties properties, List<UploadRequest> requests, String clientId) {
    try {
      log.info(
          "Uploading {} files to container: {}", requests.size(), properties.getContainerName());
      BlobContainerClient containerClient = createBlobContainerClient(properties);

      ensureContainerExists(containerClient, properties.getContainerName());

      long sharedTimestamp = System.currentTimeMillis();
      List<String> filePaths = new java.util.ArrayList<>();

      for (UploadRequest request : requests) {
        String blobName =
            generateBlobNameWithTimestamp(clientId, sharedTimestamp, request.getFileName());
        BlobClient blobClient = containerClient.getBlobClient(blobName);

        uploadToBlob(blobClient, request);
        log.info("File uploaded successfully: {}", blobName);
        filePaths.add(blobName);
      }

      return filePaths;
    } catch (Exception e) {
      log.error("Failed to upload files batch", e);
      throw ExceptionFactory.internalError(MessageConstant.E_SYS_004);
    }
  }

  /**
   * Build the blob path using target ID, current timestamp, and filename.
   *
   * @param id the ID of the user/conversation/message uploading the file
   * @param filename the name of the file being uploaded
   * @return the constructed blob path
   */
  private String generateBlobName(String id, String filename) {
    long timestamp = System.currentTimeMillis();
    return String.format("%s/%d/%s", id, timestamp, filename);
  }

  /**
   * Build the blob path using target ID, provided timestamp, and filename.
   *
   * @param id the ID of the user/conversation/message uploading the file
   * @param timestamp the timestamp to use in the path
   * @param filename the name of the file being uploaded
   * @return the constructed blob path
   */
  private String generateBlobNameWithTimestamp(String id, long timestamp, String filename) {
    return String.format("%s/%d/%s", id, timestamp, filename);
  }

  /**
   * Upload the file to Azure Blob Storage.
   *
   * @param blobClient the BlobClient for the target blob
   * @param request the upload request containing file details
   */
  private void uploadToBlob(BlobClient blobClient, UploadRequest request) {
    log.debug("Uploading file: {} (size: {} bytes)", request.getFileName(), request.getFileSize());

    BlobParallelUploadOptions options = new BlobParallelUploadOptions(request.getInputStream());
    options.setHeaders(new BlobHttpHeaders().setContentType(request.getContentType()));

    blobClient.uploadWithResponse(options, null, null);
  }

  /**
   * Verify if the blob exists; throw an exception if it does not.
   *
   * @param blobClient the BlobClient for the target blob
   * @param blobName the name of the blob
   */
  private void verifyBlobExists(BlobClient blobClient, String blobName) {
    if (Boolean.FALSE.equals(blobClient.exists())) {
      log.warn("Blob does not exist: {}", blobName);
      throw ExceptionFactory.internalError(MessageConstant.E_SYS_003);
    }
  }

  /**
   * Create a SAS token for the specified blob.
   *
   * @param blobClient the BlobClient for the target blob
   * @return the generated SAS token
   */
  private String createSasToken(BlobClient blobClient) {
    BlobSasPermission permission = new BlobSasPermission().setReadPermission(true);
    OffsetDateTime expiryTime = OffsetDateTime.now().plusMinutes(SAS_TOKEN_EXPIRY_MINUTES);
    BlobServiceSasSignatureValues sasValues =
        new BlobServiceSasSignatureValues(expiryTime, permission);

    return blobClient.generateSas(sasValues);
  }

  /**
   * Build the full URL for accessing the blob with the SAS token.
   *
   * @param blobClient the BlobClient for the target blob
   * @param sasToken the generated SAS token
   * @return the full URL with SAS token
   */
  private String buildFullUrl(BlobClient blobClient, String sasToken) {
    return blobClient.getBlobUrl() + "?" + sasToken;
  }

  /**
   * Ensure the specified container exists; create it if it does not.
   *
   * @param client the BlobContainerClient for the target container
   * @param containerName the name of the container
   */
  private void ensureContainerExists(BlobContainerClient client, String containerName) {
    if (!client.exists()) {
      client.create();
      log.info("Container '{}' created", containerName);
    } else {
      log.info("Connected to existing container: '{}'", containerName);
    }
  }

  /**
   * Create a BlobContainerClient using the provided properties.
   *
   * @param properties the Azure Blob configuration properties
   * @return the BlobContainerClient instance
   */
  private BlobContainerClient createBlobContainerClient(AzureBlobProperties properties) {
    BlobClientProperties clientProperties =
        BlobClientProperties.builder()
            .connectionString(properties.getConnectionString())
            .containerName(properties.getContainerName())
            .build();

    return blobClientFactory.createBlobContainerClient(clientProperties);
  }
}
