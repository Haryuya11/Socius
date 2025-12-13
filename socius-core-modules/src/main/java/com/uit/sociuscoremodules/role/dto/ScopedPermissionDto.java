package com.uit.sociuscoremodules.role.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Data Transfer Object (DTO) representing permissions scoped to a specific role or context. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScopedPermissionDto {
  private String scope;
  private String scopeCode;
  private String roleName;
  private List<PermissionDto> permissions;
}
