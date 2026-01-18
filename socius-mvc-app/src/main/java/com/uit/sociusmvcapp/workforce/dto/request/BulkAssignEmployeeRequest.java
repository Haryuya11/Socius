package com.uit.sociusmvcapp.workforce.dto.request;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Request object for bulk assigning employees to departments. */
@Getter
@Setter
@NoArgsConstructor
public class BulkAssignEmployeeRequest {
  private List<AssignEmployeeToDepartmentRequest> employees;
}
