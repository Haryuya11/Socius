package com.uit.sociusmvcapp.workforce.dto;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** Generic Data Transfer Object representing the result of a batch processing operation. */
@Getter
@RequiredArgsConstructor
public class BatchProcessResultDto<T> {
  private final List<T> toInsert;
  private final List<String> successfulIds;
  private final List<BatchErrorDto> errors;
}
