package com.uit.sociusmvcapp.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Data Transfer Object for uploaded file information. */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UploadFileDto {
  private String path;
  private String url;
}
