package com.uit.sociusmvcapp.iam.internal.domain;

import com.uit.sociusmvcapp.shared.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/** Permission domain model representing a system permission. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Permission extends BaseEntity {
  private String permissionCode;
  private String permissionName;
  private String resource;
  private String action;
  private String description;
}
