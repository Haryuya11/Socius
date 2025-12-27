package com.uit.sociusmvcapp.employee.internal.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.azure.storage.blob.options.BlobParallelUploadOptions;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import com.uit.sociusmvcapp.employee.AzureBlobService;
import com.uit.sociusmvcapp.shared.constants.MessageConstant;
import com.uit.sociusmvcapp.shared.request.UploadRequest;
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

  /** BlobServiceClient for Azure Blob Storage operations. */
  private final BlobServiceClient blobServiceClient;

  /**
   * Upload a file to Azure Blob Storage.
   *
   * @param request the upload request containing file details
   * @param containerName the name of the container to upload the file to
   * @return the file path in Azure Blob Storage
   */
  @Override
  public String uploadFile(UploadRequest request, String containerName) {
    try {
      BlobContainerClient containerClient = getContainerClient(containerName);
      String blobPath = buildBlobPath(request.getClientId(), request.getFileName());
      BlobClient blobClient = containerClient.getBlobClient(blobPath);
      uploadToBlob(blobClient, request);
      log.info("File uploaded successfully: {}", blobPath);
      return blobPath;
    } catch (Exception e) {
      log.error("Failed to upload file: {}", request.getFileName(), e);
      throw ExceptionFactory.internalError(MessageConstant.E_SYS_004);
    }
  }

  /**
   * Generate a SAS token for accessing a specific blob.
   *
   * @param blobName the name of the blob
   * @param containerName the name of the container
   * @return the generated SAS token
   */
  @Override
  public String generateSasToken(String blobName, String containerName) {
    try {
      BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
      BlobClient blobClient = containerClient.getBlobClient(blobName);
      verifyBlobExists(blobClient, blobName);

      String sasToken = createSasToken(blobClient);
      String fullUrl = buildFullUrl(blobClient, sasToken);

      log.info("Generated SAS token for blob: {}", blobName);
      return fullUrl;

    } catch (Exception e) {
      log.error("Failed to generate SAS token for blob: {}", blobName, e);
      throw ExceptionFactory.internalError("Không thể tạo SAS token");
    }
  }

  /**
   * Build the blob path using user ID, current timestamp, and filename.
   *
   * @param userId the ID of the user uploading the file
   * @param filename the name of the file being uploaded
   * @return the constructed blob path
   */
  private String buildBlobPath(String userId, String filename) {
    long timestamp = System.currentTimeMillis();
    return String.format("%s/%d/%s", userId, timestamp, filename);
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
      log.error("Blob not found: {}", blobName);
      throw ExceptionFactory.internalError("File không tồn tại");
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
   * Initialize the BlobContainerClient and ensure the container exists.
   *
   * @param containerName the name of the container
   * @return the initialized BlobContainerClient
   */
  private BlobContainerClient getContainerClient(String containerName) {
    try {
      log.info("Initializing Azure Blob Storage connection to container: '{}'", containerName);

      BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
      ensureContainerExists(containerClient, containerName);
      return containerClient;
    } catch (Exception e) {
      log.error("CRITICAL: Failed to connect to Azure Blob Storage", e);
      throw ExceptionFactory.internalError(MessageConstant.E_SYS_003);
    }
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
}
