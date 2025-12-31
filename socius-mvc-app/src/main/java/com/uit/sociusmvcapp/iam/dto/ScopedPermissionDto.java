package com.uit.sociusmvcapp.iam.dto;

import java.io.Serial;
import java.io.Serializable;
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
public class ScopedPermissionDto implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
  private String scope;
  private String resourceCode;
  private String permissionCode;
}
