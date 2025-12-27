package com.uit.sociusmvcapp.iam.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Data Transfer Object representing a role along with its associated permissions. */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleWithPermissionsDto {
  private String roleCode;
  private String roleName;
  private String roleType;
  private String description;
}
