package com.uit.sociuscoremodules.employee.domain;

import com.uit.sociuscoremodules.employee.dto.PermissionDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ScopedPermissionDto domain model representing permissions scoped to a specific role or context.
 */
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
