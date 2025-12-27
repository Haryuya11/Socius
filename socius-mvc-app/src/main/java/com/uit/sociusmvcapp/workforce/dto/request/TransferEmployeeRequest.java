package com.uit.sociusmvcapp.workforce.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Request object for transferring an employee between departments. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransferEmployeeRequest {
  private String employeeId;
  private String roleCode;
  private Boolean isPrimary;
  private String fromDepartmentCode;
  private String toDepartmentCode;
}
