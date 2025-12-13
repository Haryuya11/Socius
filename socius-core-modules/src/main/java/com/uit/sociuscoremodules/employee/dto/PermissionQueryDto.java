package com.uit.sociuscoremodules.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Data Transfer Object for querying permissions based on various criteria. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PermissionQueryDto {
  private String roleType;
  private String roleCode;
  private String roleName;
  private String scopeCode;
  private String permissionCode;
  private String permissionName;
  private String resource;
  private String action;
  private String description;
}
