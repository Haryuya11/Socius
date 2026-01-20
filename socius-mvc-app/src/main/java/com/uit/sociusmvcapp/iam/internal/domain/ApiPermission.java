package com.uit.sociusmvcapp.iam.internal.domain;

import com.uit.sociusmvcapp.shared.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Domain model representing the mapping between an API endpoint and its required permission. Used
 * for database-driven RBAC authorization.
 */
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ApiPermission extends BaseEntity {
  private String permissionCode;
  private String resource;
  private String action;
  private String httpMethod;
  private String urlPattern;
}
