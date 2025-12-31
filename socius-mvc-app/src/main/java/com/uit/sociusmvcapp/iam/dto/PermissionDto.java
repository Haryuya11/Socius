package com.uit.sociusmvcapp.iam.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Data Transfer Object representing a permission. */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PermissionDto {
  private String permissionCode;
  private String permissionName;
  private String resource;
  private String action;
  private String description;
}
