package com.uit.sociusmvcapp.employee.internal.config;

import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Configuration class for Azure Blob Storage integration. */
@Configuration
public class AzureBlobConfig {

  /** Azure Blob Storage connection string. */
  @Value("${azure.blob.connection-string}")
  private String connectionString;

  /**
   * Creates a BlobServiceClient bean for interacting with Azure Blob Storage.
   *
   * @return the BlobServiceClient instance
   */
  @Bean
  public BlobServiceClient blobServiceClient() {
    return new BlobServiceClientBuilder().connectionString(connectionString).buildClient();
  }
}
