package com.uit.sociuscoremodules.employee.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Permission domain model representing a system permission. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Permission {
  private String permissionCode;
  private String permissionName;
  private String resource;
  private String action;
  private String description;
}
