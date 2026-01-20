package com.uit.sociusmvcapp.iam.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** DTO containing the result of a cache clear operation. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CacheClearResultDto {

  /** List of cache names that were cleared. */
  private List<String> clearedCaches;

  /** Timestamp when the cache was cleared. */
  private LocalDateTime clearedAt;
}
