package com.uit.sociusmvcapp.workforce.internal.domain.summary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** EmployeeSummary domain model representing a summary of Employee information. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeSummary {
  private String clientId;
  private String userId;
  private String firstName;
  private String lastName;
  private String systemRole;
  private String imageUrl;
  private Long salary;
}
