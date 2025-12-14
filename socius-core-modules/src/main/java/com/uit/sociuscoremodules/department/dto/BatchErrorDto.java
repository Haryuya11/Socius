package com.uit.sociuscoremodules.department.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Data Transfer Object representing batch error details. */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BatchErrorDto {
  private String id;
  private String message;
}
