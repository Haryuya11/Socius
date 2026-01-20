package com.uit.sociusmvcapp.azure.blob;

import java.io.OutputStream;
import java.util.List;

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

  /**
   * Download a single file from Azure Blob Storage.
   *
   * @param properties Azure Blob configuration properties
   * @param blobName the name of the blob to download
   * @param outputStream the output stream to write the file content
   */
  void downloadFile(AzureBlobProperties properties, String blobName, OutputStream outputStream);

  /**
   * Download multiple files from Azure Blob Storage as a ZIP archive.
   *
   * @param properties Azure Blob configuration properties
   * @param blobNames the list of blob names to download
   * @param outputStream the output stream to write the ZIP content
   */
  void downloadFilesAsZip(
      AzureBlobProperties properties, List<String> blobNames, OutputStream outputStream);

  /**
   * Get the original file name from a blob path.
   *
   * @param blobName the full blob name/path
   * @return the original file name
   */
  String getOriginalFileName(String blobName);

  /**
   * Get the content type (MIME type) of a blob from Azure.
   *
   * @param properties Azure Blob configuration properties
   * @param blobName the name of the blob
   * @return the content type of the blob, or "application/octet-stream" if not available
   */
  String getContentType(AzureBlobProperties properties, String blobName);

  /**
   * Upload multiple files to Azure Blob Storage with a shared timestamp folder.
   *
   * @param properties Azure Blob configuration properties
   * @param requests the list of upload requests containing file details
   * @param clientId the client ID (conversation ID) for organizing files
   * @return the list of file paths for uploaded files
   */
  List<String> uploadFiles(
      AzureBlobProperties properties, List<UploadRequest> requests, String clientId);
}
