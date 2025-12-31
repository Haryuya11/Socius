package com.uit.sociusmvcapp.iam.internal.domain;

import com.uit.sociusmvcapp.shared.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * RolePermission domain model representing the many-to-many relationship between Role and
 * Permission.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class RolePermission extends BaseEntity {
  private String roleCode;
  private String permissionCode;
}
