package com.uit.sociusmvcapp.workforce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Error details for failed batch operations. */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BatchErrorDto {
  private String clientId;
  private String errorCode;
  private String errorMessage;
}
