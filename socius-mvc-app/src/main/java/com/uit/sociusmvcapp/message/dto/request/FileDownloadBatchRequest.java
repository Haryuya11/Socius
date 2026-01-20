package com.uit.sociusmvcapp.message.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Request object for downloading multiple files as a ZIP archive. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FileDownloadBatchRequest {

  @NotEmpty(message = "File list cannot be empty")
  @Valid
  private List<FileDownloadRequest> files;
}
