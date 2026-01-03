package com.uit.sociusmvcapp.azure.blob;

import java.io.InputStream;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * UploadRequest represents a request to upload a file, containing necessary metadata and the file
 * content.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UploadRequest {
  private String clientId;
  private String fileName;
  private String contentType;
  private Long fileSize;
  private InputStream inputStream;
}
