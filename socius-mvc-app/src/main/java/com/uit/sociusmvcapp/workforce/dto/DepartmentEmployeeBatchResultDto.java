package com.uit.sociusmvcapp.workforce.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing the result of a batch operation for adding employees to a
 * department.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentEmployeeBatchResultDto {
  private List<String> successful;
  private List<BatchErrorDto> failed;
}
