package com.uit.sociuscoremodules.employee.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Employee Search Data Transfer Object (DTO) for searching employee information. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchEmployeeDto {
  private String clientId;
  private String userId;
  private String firstName;
  private String lastName;
  private String systemRole;
  private String imageUrl;

  List<EmployeeDepartmentDto> departments;
  List<EmployeeTeamDto> teams;
}
