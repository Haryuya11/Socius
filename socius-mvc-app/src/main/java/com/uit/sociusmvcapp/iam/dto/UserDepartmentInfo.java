package com.uit.sociusmvcapp.iam.dto;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * UserDepartmentInfo Data Transfer Object (DTO) representing user-department relationship
 * information.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDepartmentInfo implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  private String departmentCode;
  private String departmentName;
  private String roleCode;
  private Boolean isPrimary;
}
