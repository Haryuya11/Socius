package com.uit.sociuscoremodules.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Data Transfer Object for Permission information. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PermissionDto {
  private String permissionCode;
  private String permissionName;
  private String resource;
  private String action;
  private String description;
}
