package com.uit.sociusmvcapp.iam;

import com.uit.sociusmvcapp.iam.dto.UserPrincipal;
import com.uit.sociusmvcapp.shared.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/** UserContentProvider retrieves the authenticated user's information from the security context. */
@Component
public class UserContentProvider {

  /**
   * Get the authenticated user's content.
   *
   * @return EmployeeDto of the authenticated user
   * @throws RuntimeException if the user is not authenticated
   */
  public UserPrincipal getUserContent() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null
        && authentication.isAuthenticated()
        && authentication.getPrincipal() instanceof UserPrincipal employeeDto) {
      return employeeDto;
    }
    throw new BusinessException(HttpStatus.UNAUTHORIZED, "User is not authenticated");
  }
}
