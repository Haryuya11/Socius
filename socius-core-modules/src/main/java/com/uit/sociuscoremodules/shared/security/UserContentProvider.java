package com.uit.sociuscoremodules.shared.security;

import com.uit.sociuscoremodules.employee.dto.EmployeeDto;
import com.uit.sociuscoremodules.shared.exception.BusinessException;
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
  public EmployeeDto getUserContent() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null
        && authentication.isAuthenticated()
        && authentication.getPrincipal() instanceof EmployeeDto employeedto) {
      return employeedto;
    }
    throw new BusinessException(HttpStatus.UNAUTHORIZED, "User is not authenticated");
  }
}
