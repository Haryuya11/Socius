package com.uit.sociusmvcapp.message.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Data Transfer Object for file metadata in messages. */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class FileMetadataDto {
  private String fileName;
  private String filePath;
  private Long fileSize;
  private String mimeType;
  private String fileUrl;
}
