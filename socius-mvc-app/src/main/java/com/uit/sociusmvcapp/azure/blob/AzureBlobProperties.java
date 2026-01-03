package com.uit.sociusmvcapp.azure.blob;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/** Properties required to configure Azure Blob Storage client. */
@Getter
@Builder
@AllArgsConstructor
@EqualsAndHashCode
public class AzureBlobProperties {

  /** Azure Blob Storage connection string. */
  private final String connectionString;

  /** Container name for blob operations. */
  private final String containerName;
}
