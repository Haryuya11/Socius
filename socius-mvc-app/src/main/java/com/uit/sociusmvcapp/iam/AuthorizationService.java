package com.uit.sociusmvcapp.iam;

import com.uit.sociusmvcapp.iam.dto.UserPrincipal;

/** Service interface for user authorization. */
public interface AuthorizationService {

  /**
   * Authorize user by clientId.
   *
   * @param clientId the user id
   * @return the employee dto
   */
  UserPrincipal authorize(String clientId);
}
