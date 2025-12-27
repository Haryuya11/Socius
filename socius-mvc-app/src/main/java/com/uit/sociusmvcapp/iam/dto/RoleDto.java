package com.uit.sociusmvcapp.iam.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Data Transfer Object representing a role. */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleDto {
  private String roleCode;
  private String roleName;
  private String roleType;
  private String description;
  private List<PermissionDto> permissions;
}
