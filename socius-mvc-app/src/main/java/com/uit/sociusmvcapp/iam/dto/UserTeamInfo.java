package com.uit.sociusmvcapp.iam.dto;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** UserTeamInfo Data Transfer Object (DTO) representing user-team relationship information. */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserTeamInfo implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  private String teamCode;
  private String teamName;
  private String roleCode;
  private Boolean isLeader;
}
