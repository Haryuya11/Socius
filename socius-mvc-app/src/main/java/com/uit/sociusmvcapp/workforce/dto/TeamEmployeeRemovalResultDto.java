package com.uit.sociusmvcapp.workforce.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Result object for batch remove employee operations. */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeamEmployeeRemovalResultDto {
  private List<String> removedEmployeeIds;
  private List<BatchErrorDto> failed;
}
