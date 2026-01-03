package com.uit.sociusmvcapp.azure.blob.internal.factory;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.uit.sociusmvcapp.azure.blob.internal.domain.BlobClientProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/** Factory class for creating Azure Blob Storage clients. */
@Component
@Slf4j
public class BlobClientFactory {

  /**
   * Create a BlobServiceClient for interacting with Azure Blob Storage.
   *
   * @param properties the properties for building the client
   * @return BlobServiceClient instance
   */
  public BlobServiceClient createBlobServiceClient(BlobClientProperties properties) {
    log.debug("Creating new BlobServiceClient");

    return new BlobServiceClientBuilder()
        .connectionString(properties.getConnectionString())
        .buildClient();
  }

  /**
   * Create a BlobContainerClient for a specific container.
   *
   * @param properties the properties for building the client
   * @return BlobContainerClient instance
   */
  public BlobContainerClient createBlobContainerClient(BlobClientProperties properties) {
    log.debug("Creating new BlobContainerClient for container: {}", properties.getContainerName());

    BlobServiceClient serviceClient = createBlobServiceClient(properties);
    return serviceClient.getBlobContainerClient(properties.getContainerName());
  }

  /**
   * Create a BlobServiceClient using SAS token authentication.
   *
   * @param endpoint the blob service endpoint URL
   * @param sasToken the SAS token for authentication
   * @return BlobServiceClient instance
   */
  public BlobServiceClient createBlobServiceClientWithSas(String endpoint, String sasToken) {
    log.debug("Creating new BlobServiceClient with SAS token");

    return new BlobServiceClientBuilder().endpoint(endpoint).sasToken(sasToken).buildClient();
  }

  /**
   * Create a BlobServiceClient using Azure AD authentication.
   *
   * @param endpoint the blob service endpoint URL
   * @param credential the Azure credential for authentication
   * @return BlobServiceClient instance
   */
  public BlobServiceClient createBlobServiceClientWithCredential(
      String endpoint, com.azure.core.credential.TokenCredential credential) {
    log.debug("Creating new BlobServiceClient with Azure AD credential");

    return new BlobServiceClientBuilder().endpoint(endpoint).credential(credential).buildClient();
  }
}
