package com.uit.sociuscoremodules.teamemployee.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Request object for transferring an employee between teams. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransferTeamEmployeeRequest {
  private String employeeId;
  private String roleCode;
  private Boolean isLeader;
  private String fromTeamCode;
  private String toTeamCode;
}
