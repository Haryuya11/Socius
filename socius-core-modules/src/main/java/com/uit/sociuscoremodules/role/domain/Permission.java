package com.uit.sociuscoremodules.role.domain;

import java.time.LocalDateTime;
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
  private Integer id;
  private String permissionCode;
  private String permissionName;
  private String resource;
  private String action;
  private String description;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private LocalDateTime deletedAt;
  private Short deleteFlag;
}
