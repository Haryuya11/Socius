package com.uit.sociusmvcapp.message.internal.adapter;

import com.uit.sociusmvcapp.azure.blob.UploadRequest;
import lombok.Builder;
import lombok.Getter;

/**
 * Internal context object to hold file metadata during upload process. Avoids parallel collections
 * code smell by bundling related data together.
 */
@Getter
@Builder
class FileUploadContext {
  private final String fileName;
  private final Long fileSize;
  private final String mimeType;
  private final UploadRequest uploadRequest;
}
