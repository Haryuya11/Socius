package com.uit.sociusmvcapp.employee;

import com.uit.sociusmvcapp.shared.request.UploadRequest;

/** Service interface for interacting with Azure Blob Storage. */
public interface AzureBlobService {
  /**
   * Upload a file to Azure Blob Storage.
   *
   * @param request the upload request containing file details
   * @param containerName the name of the container to upload the file to
   * @return the file path in Azure Blob Storage
   */
  String uploadFile(UploadRequest request, String containerName);

  /**
   * Generate a SAS token for accessing a specific blob.
   *
   * @param blobName the name of the blob
   * @param containerName the name of the container
   * @return the generated SAS token
   */
  String generateSasToken(String blobName, String containerName);
}
