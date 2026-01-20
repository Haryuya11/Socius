package com.uit.sociusmvcapp.message.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO containing file information needed for download, including file name and content type.
 * Consolidates multiple service calls into one to reduce Azure blob calls.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileDownloadInfoDto {

  /** The original file name. */
  private String fileName;

  /** The content type (MIME type) of the file. */
  private String contentType;
}
