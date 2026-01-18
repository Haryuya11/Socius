package com.uit.sociusmvcapp.iam.internal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object representing the mapping between an API endpoint and its required
 * permission.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiPermissionDto {
  private String permissionCode;
  private String resource;
  private String action;
  private String httpMethod;
  private String urlPattern;
}
