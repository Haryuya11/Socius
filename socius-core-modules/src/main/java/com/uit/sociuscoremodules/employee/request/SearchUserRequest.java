package com.uit.sociuscoremodules.employee.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Request object for searching users based on various criteria. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchUserRequest {
  private String clientId;
  private String userId;
  private String fullName;
  private String systemRole;
}
