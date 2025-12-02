package com.uit.sociuscoremodules.employee.converter;

import com.microsoft.graph.models.ObjectIdentity;
import com.microsoft.graph.models.PasswordProfile;
import com.microsoft.graph.models.User;
import com.uit.sociuscoremodules.employee.constants.EmployeeConstant;
import com.uit.sociuscoremodules.employee.domain.Employee;
import com.uit.sociuscoremodules.employee.dto.EmployeeDto;
import com.uit.sociuscoremodules.employee.request.EmployeeCreateRequest;
import com.uit.sociuscoremodules.shared.constants.CommonConstant;
import java.util.Collections;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/** Converter for Employee entity, DTO, and Microsoft Graph User objects. */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EmployeeConverter {

  /**
   * Converts an Employee entity to an EmployeeDto.
   *
   * @param employee the Employee entity
   * @return the corresponding EmployeeDto
   */
  EmployeeDto entityToDto(Employee employee);

  /**
   * Converts a list of Employee entities to a list of EmployeeDtos.
   *
   * @param employees the list of Employee entities
   * @return the corresponding list of EmployeeDtos
   */
  List<EmployeeDto> entityToDto(List<Employee> employees);

  /**
   * Converts a UserCreateRequest to a Microsoft Graph User object. This method requires the
   * tenant's issuer domain (e.g., "your-tenant.onmicrosoft.com") to correctly build the identities.
   *
   * @param request The user creation request DTO.
   * @param issuer the tenant's issuer domain.
   * @return A fully formed Microsoft Graph User object ready for creation.
   */
  @Mapping(target = "displayName", expression = "java(buildDisplayName(request))")
  @Mapping(target = "givenName", expression = "java(request.getFirstName())")
  @Mapping(target = "surname", expression = "java(request.getLastName())")
  @Mapping(
      target = "userPrincipalName",
      expression = "java(buildUserPrincipalName(request.getUserId(), issuer))")
  @Mapping(target = "mailNickname", expression = "java(extractMailNickname(request.getUserId()))")
  //  @Mapping(target = "mail", expression = "java(request.getUserId())")
  @Mapping(target = "department", expression = "java(request.getDepartmentCode())")
  @Mapping(target = "accountEnabled", constant = "true")
  @Mapping(target = "passwordProfile", expression = "java(createDefaultPasswordProfile())")
  @Mapping(target = "passwordPolicies", constant = "DisablePasswordExpiration")
  @Mapping(
      target = "identities",
      expression = "java(createEmailIdentity(request.getUserId(), issuer))")
  @Mapping(target = "mail", ignore = true)
  User toGraphUser(EmployeeCreateRequest request, String issuer);

  /**
   * Converts an EmployeeCreateRequest to a Microsoft Graph User object for an update operation.
   * This mapping is intentionally sparse, only including fields that are typically updated, such as
   * display name and department. It omits sensitive or immutable fields like passwordProfile,
   * userPrincipalName, and accountEnabled.
   *
   * @param request The DTO containing the user details to update.
   * @return A Microsoft Graph User object containing only the fields to be patched.
   */
  @Mapping(target = "displayName", expression = "java(buildDisplayName(request))")
  @Mapping(target = "givenName", expression = "java(request.getFirstName())")
  @Mapping(target = "surname", expression = "java(request.getLastName())")
  @Mapping(target = "department", expression = "java(request.getDepartmentCode())")
  User toGraphUserForUpdate(EmployeeCreateRequest request);

  // --- Helper Default Methods ---

  /**
   * Builds the display name by concatenating first and last names.
   *
   * @param request The EmployeeCreateRequest containing first and last names.
   * @return The constructed display name.
   */
  default String buildDisplayName(EmployeeCreateRequest request) {
    if (request == null) {
      return "";
    }
    String fn = request.getFirstName() == null ? "" : request.getFirstName().trim();
    String ln = request.getLastName() == null ? "" : request.getLastName().trim();
    return (fn + " " + ln).trim();
  }

  /**
   * Extracts the mail nickname from the user ID (email address).
   *
   * @param userId The user's email address.
   * @return The mail nickname (part before '@').
   */
  default String extractMailNickname(String userId) {
    if (userId == null || userId.isEmpty()) {
      return null;
    }
    return userId.contains(CommonConstant.AT_SIGN)
        ? userId.substring(CommonConstant.INIT_INDEX, userId.indexOf(CommonConstant.AT_SIGN))
        : userId;
  }

  /**
   * Builds the user principal name using the mail nickname and issuer domain.
   *
   * @param userId The user's email address.
   * @param issuer The tenant's issuer domain.
   * @return The constructed user principal name.
   */
  default String buildUserPrincipalName(String userId, String issuer) {
    String username = extractMailNickname(userId);
    return username + CommonConstant.AT_SIGN + issuer;
  }

  /**
   * Creates a default PasswordProfile with a preset password and force change on next sign-in.
   *
   * @return A PasswordProfile object with default settings.
   */
  default PasswordProfile createDefaultPasswordProfile() {
    PasswordProfile pp = new PasswordProfile();
    pp.setPassword(EmployeeConstant.DEFAULT_PASSWORD);
    pp.setForceChangePasswordNextSignIn(Boolean.TRUE);
    return pp;
  }

  /**
   * Creates the list of identities required for a user to sign in with their email address.
   *
   * @param userId The user's email address.
   * @param issuer The tenant's issuer domain.
   * @return A list containing the ObjectIdentity for email-based sign-in.
   */
  default List<ObjectIdentity> createEmailIdentity(String userId, String issuer) {
    if (userId == null || userId.isEmpty()) {
      return Collections.emptyList();
    }

    ObjectIdentity emailIdentity = new ObjectIdentity();
    emailIdentity.setSignInType(EmployeeConstant.SIGN_IN_TYPE_EMAIL_ADDRESS);
    emailIdentity.setIssuer(issuer);
    emailIdentity.setIssuerAssignedId(userId);

    return Collections.singletonList(emailIdentity);
  }
}
