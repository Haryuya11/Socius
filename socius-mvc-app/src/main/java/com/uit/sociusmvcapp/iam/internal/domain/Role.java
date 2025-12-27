package com.uit.sociusmvcapp.iam.internal.domain;

import com.uit.sociusmvcapp.shared.domain.BaseEntity;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/** Domain model representing a role within the system. */
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Role extends BaseEntity {
  private String roleCode;
  private String roleName;
  private String roleType;
  private String description;

  private List<Permission> permissions;
}
