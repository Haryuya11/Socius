package com.uit.sociusmvcapp.iam.internal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Data Transfer Object representing the association between a role and a permission. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RolePermissionDto {
  private String roleCode;
  private String permissionCode;
}
