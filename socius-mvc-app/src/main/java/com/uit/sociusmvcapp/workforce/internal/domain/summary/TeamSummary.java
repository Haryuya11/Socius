package com.uit.sociusmvcapp.workforce.internal.domain.summary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** TeamSummary domain model representing a summary of Team information. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeamSummary {
  private String teamCode;
  private String teamName;
  private String departmentCode;
}
