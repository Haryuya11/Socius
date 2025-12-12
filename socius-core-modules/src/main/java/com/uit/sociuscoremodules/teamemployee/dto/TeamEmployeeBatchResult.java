package com.uit.sociuscoremodules.teamemployee.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Result object for batch add employee operations. */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeamEmployeeBatchResult {
  private List<TeamEmployeeDto> successful;
  private List<BatchError> failed;

  /** Error details for failed batch operations. */
  @Getter
  @Setter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class BatchError {
    private String clientId;
    private String errorCode;
    private String errorMessage;
  }
}
