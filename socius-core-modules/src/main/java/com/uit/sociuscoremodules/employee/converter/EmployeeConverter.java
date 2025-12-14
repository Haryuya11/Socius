package com.uit.sociuscoremodules.employee.converter;

import com.microsoft.graph.models.ObjectIdentity;
import com.microsoft.graph.models.PasswordProfile;
import com.microsoft.graph.models.User;
import com.uit.sociuscoremodules.employee.constants.EmployeeConstant;
import com.uit.sociuscoremodules.employee.domain.Employee;
import com.uit.sociuscoremodules.employee.domain.EmployeeDepartment;
import com.uit.sociuscoremodules.employee.domain.EmployeeTeam;
import com.uit.sociuscoremodules.employee.dto.EmployeeDepartmentDto;
import com.uit.sociuscoremodules.employee.dto.EmployeeDto;
import com.uit.sociuscoremodules.employee.dto.EmployeeProfileDto;
import com.uit.sociuscoremodules.employee.dto.EmployeeTeamDto;
import com.uit.sociuscoremodules.employee.dto.SearchEmployeeDto;
import com.uit.sociuscoremodules.employee.request.EmployeeCreateRequest;
import com.uit.sociuscoremodules.shared.constants.CommonConstant;
import java.util.Collections;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
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
  List<EmployeeDto> entitiesToDtos(List<Employee> employees);

  /**
   * Converts an Employee entity to an EmployeeProfileDto.
   *
   * @param employee the Employee entity
   * @return the corresponding EmployeeProfileDto
   */
  EmployeeProfileDto entityToProfileDto(Employee employee);

  /**
   * Converts an EmployeeDto to an EmployeeProfileDto.
   *
   * @param employeeDto the EmployeeDto
   * @return the corresponding EmployeeProfileDto
   */
  EmployeeProfileDto dtoToProfileDto(EmployeeDto employeeDto);

  /**
   * Converts an EmployeeDepartment entity to an EmployeeDepartmentDto.
   *
   * @param department the EmployeeDepartment entity
   * @return the corresponding EmployeeDepartmentDto
   */
  EmployeeDepartmentDto departmentToDto(EmployeeDepartment department);

  /**
   * Converts an EmployeeTeam entity to an EmployeeTeamDto.
   *
   * @param team the EmployeeTeam entity
   * @return the corresponding EmployeeTeamDto
   */
  EmployeeTeamDto teamToDto(EmployeeTeam team);

  /**
   * Converts a list of Employee entities to a list of SearchEmployeeDtos.
   *
   * @param employees the list of Employee entities
   * @return the corresponding list of SearchEmployeeDtos
   */
  List<SearchEmployeeDto> entitiesToSearchDtos(List<Employee> employees);

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
  @Mapping(target = "accountEnabled", constant = "true")
  @Mapping(target = "passwordProfile", expression = "java(createDefaultPasswordProfile())")
  @Mapping(target = "passwordPolicies", constant = "DisablePasswordExpiration")
  @Mapping(
      target = "identities",
      expression = "java(createEmailIdentity(request.getUserId(), issuer))")
  @Mapping(target = "mail", expression = "java(request.getUserId())")
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
  User toGraphUserForUpdate(EmployeeCreateRequest request);

  /**
   * Converts an EmployeeCreateRequest to an Employee entity.
   *
   * @param request The DTO containing the user creation details.
   * @return An Employee entity populated with the request data.
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "deletedAt", ignore = true)
  @Mapping(target = "deleteFlag", ignore = true)
  @Mapping(target = "departments", ignore = true)
  @Mapping(target = "teams", ignore = true)
  @Mapping(target = "permissions", ignore = true)
  Employee createRequestToEntity(EmployeeCreateRequest request);

  // --- Helper Default Methods ---

  /**
   * Builds the display name by concatenating first and last names.
   *
   * @param request The EmployeeCreateRequest containing first and last names.
   * @return The constructed display name.
   */
  @Named("buildDisplayName")
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
  @Named("extractMailNickname")
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
  @Named("buildUserPrincipalName")
  default String buildUserPrincipalName(String userId, String issuer) {
    String username = extractMailNickname(userId);
    return username + CommonConstant.AT_SIGN + issuer;
  }

  /**
   * Creates a default PasswordProfile with a preset password and force change on next sign-in.
   *
   * @return A PasswordProfile object with default settings.
   */
  @Named("createDefaultPasswordProfile")
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
  @Named("createEmailIdentity")
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
