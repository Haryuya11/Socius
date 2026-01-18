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
import java.time.OffsetDateTime;
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
