package com.uit.sociusmvcapp.azure.blob;

/** Service interface for Azure Blob Storage operations. */
public interface AzureBlobService {

  /**
   * Upload a file to Azure Blob Storage.
   *
   * @param properties Azure Blob configuration properties
   * @param request the upload request containing file details
   * @return the path of the uploaded file
   */
  String uploadFile(AzureBlobProperties properties, UploadRequest request);

  /**
   * Generate a SAS token for accessing a blob.
   *
   * @param properties Azure Blob configuration properties
   * @param blobName the name of the blob
   * @return the SAS token URL
   */
  String generateSasToken(AzureBlobProperties properties, String blobName);

  /**
   * Delete a blob from Azure Blob Storage.
   *
   * @param properties Azure Blob configuration properties
   * @param blobName the name of the blob to delete
   */
  void deleteFile(AzureBlobProperties properties, String blobName);

  /**
   * Check if a blob exists.
   *
   * @param properties Azure Blob configuration properties
   * @param blobName the name of the blob
   * @return true if the blob exists, false otherwise
   */
  boolean fileExists(AzureBlobProperties properties, String blobName);
}
