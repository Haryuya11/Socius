package com.uit.sociusmvcapp.employee.dto.request;

import lombok.Getter;
import lombok.Setter;

/**
 * Request DTO for updating an employee's system role.
 *
 * <p>This is a separate request to ensure only SYS_ADMIN can change system roles.
 */
@Getter
@Setter
public class UpdateSystemRoleRequest {

  /** The new system role for the employee (e.g., "SYS_ADMIN", "USER"). */
  private String systemRole;
}
