package com.uit.sociuscoremodules.role.domain;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Domain model representing a role within the system. */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Role {
  private Integer id;
  private String roleCode;
  private String roleName;
  private String roleType;
  private String description;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private LocalDateTime deletedAt;
  private Short deleteFlag;

  private List<Permission> permissions;
}
