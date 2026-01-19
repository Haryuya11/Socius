package com.uit.sociusmvcapp.message.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Request object for downloading a file. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FileDownloadRequest {

  @NotBlank(message = "File path is required")
  private String filePath;
}
