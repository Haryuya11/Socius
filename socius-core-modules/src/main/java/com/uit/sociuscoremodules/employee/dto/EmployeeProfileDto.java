package com.uit.sociuscoremodules.employee.dto;

import com.uit.sociuscoremodules.employee.domain.ScopedPermissionDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Employee Data Transfer Object (DTO) representing employee information. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeProfileDto {
  private String clientId;
  private String userId;
  private String firstName;
  private String lastName;
  private String systemRole;
  private Long salary;
  private String imageUrl;

  private List<EmployeeDepartmentDto> departments;
  private List<EmployeeTeamDto> teams;
  private List<ScopedPermissionDto> permissions;
}
