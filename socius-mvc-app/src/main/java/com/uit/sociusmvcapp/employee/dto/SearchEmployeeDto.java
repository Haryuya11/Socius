package com.uit.sociusmvcapp.employee.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Employee Search Data Transfer Object (DTO) for searching employee information. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchEmployeeDto {
  private String clientId;
  private String userId;
  private String firstName;
  private String lastName;
  private String systemRole;
  private String imageUrl;

  private List<DepartmentInfo> departments;
  private List<TeamInfo> teams;

  // ==========================================
  // Inner Classes (Self-contained)
  // ==========================================

  /** Department Information Inner Class. */
  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class DepartmentInfo {
    private String departmentCode;
    private String departmentName;
    private String roleCode;
    private Boolean isPrimary;
  }

  /** Team Information Inner Class. */
  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class TeamInfo {
    private String teamCode;
    private String teamName;
    private String roleCode;
    private Boolean isLeader;
  }
}
