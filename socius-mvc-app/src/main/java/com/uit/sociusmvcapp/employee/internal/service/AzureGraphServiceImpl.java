package com.uit.sociusmvcapp.employee.internal.service;

import com.microsoft.graph.models.PasswordProfile;
import com.microsoft.graph.models.User;
import com.microsoft.graph.serviceclient.GraphServiceClient;
import com.uit.sociusmvcapp.employee.AzureGraphService;
import com.uit.sociusmvcapp.employee.dto.request.ChangePasswordRequest;
import com.uit.sociusmvcapp.employee.dto.request.EmployeeCreateRequest;
import com.uit.sociusmvcapp.employee.internal.constants.GraphUserFields;
import com.uit.sociusmvcapp.employee.internal.converter.EmployeeConverter;
import com.uit.sociusmvcapp.shared.constants.MessageConstant;
import com.uit.sociusmvcapp.shared.service.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** Implementation of AzureGraphService for interacting with Microsoft Graph API. */
@Service
@RequiredArgsConstructor
@Slf4j
public class AzureGraphServiceImpl implements AzureGraphService {

  /** Converter for transforming Employee data to Graph User format. */
  private final EmployeeConverter employeeConverter;

  /** GraphServiceClient for interacting with Microsoft Graph API. */
  private final GraphServiceClient graphClient;

  /** Default fields to select when retrieving user information from Azure Graph API. */
  private static final String[] DEFAULT_USER_FIELDS = {
    GraphUserFields.ID,
    GraphUserFields.DISPLAY_NAME,
    GraphUserFields.GIVEN_NAME,
    GraphUserFields.SURNAME,
    GraphUserFields.MAIL,
    GraphUserFields.USER_PRINCIPAL_NAME,
    GraphUserFields.DEPARTMENT,
    GraphUserFields.ACCOUNT_ENABLED,
    GraphUserFields.LAST_PASSWORD_CHANGE_DATE_TIME
  };

  /** Domain name used as issuer for user creation. */
  @Value("${azure.domain.name}")
  private String issuer;

  /**
   * Create a new user in Azure Active Directory.
   *
   * @param request the request containing user creation details
   * @return the created User object
   */
  @Override
  public User createUser(EmployeeCreateRequest request) {
    try {
      User newUser = employeeConverter.toGraphUser(request, issuer);
      log.info("New user principalName={}, issuer={}", newUser.getUserPrincipalName(), issuer);
      return graphClient.users().post(newUser);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw ExceptionFactory.badRequest(MessageConstant.E_EMP_001);
    }
  }

  /**
   * Update an existing user in Azure Active Directory.
   *
   * @param clientId the client ID of the user to update
   * @param request the request containing user update details
   */
  @Override
  public void updateUser(String clientId, EmployeeCreateRequest request) {
    try {
      User updatedUser = employeeConverter.toGraphUserForUpdate(request);
      graphClient.users().byUserId(clientId).patch(updatedUser);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw ExceptionFactory.badRequest(MessageConstant.E_EMP_002);
    }
  }

  /**
   * Deactivate a user in Azure Active Directory.
   *
   * @param clientId the client ID of the user to deactivate
   */
  @Override
  public void deactivateUser(String clientId) {
    try {
      User user = findById(clientId);
      if (user != null && !Boolean.FALSE.equals(user.getAccountEnabled())) {
        user.setAccountEnabled(false);
        graphClient.users().byUserId(clientId).patch(user);
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw ExceptionFactory.badRequest(MessageConstant.E_EMP_003);
    }
  }

  /**
   * Find a user by their client ID in Azure Active Directory.
   *
   * @param clientId the client ID of the user to find
   * @return the User object
   */
  @Override
  public User findById(String clientId) {
    try {
      return graphClient
          .users()
          .byUserId(clientId)
          .get(
              r -> {
                assert r.queryParameters != null;
                r.queryParameters.select = DEFAULT_USER_FIELDS;
              });
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw ExceptionFactory.badRequest(MessageConstant.E_EMP_004);
    }
  }

  /**
   * Reactivate a user in Azure Graph.
   *
   * @param clientId the client ID of the user to reactivate
   * @param request the user creation request
   */
  @Override
  public void reactivateUser(String clientId, EmployeeCreateRequest request) {
    try {
      User user = findById(clientId);
      User updatedUser = employeeConverter.toGraphUserForUpdate(request);
      updatedUser.setId(user.getId());
      updatedUser.setAccountEnabled(true);

      graphClient.users().byUserId(clientId).patch(updatedUser);
    } catch (Exception e) {
      log.error("Failed to reactivate user: {}", e.getMessage(), e);
      throw ExceptionFactory.badRequest(MessageConstant.E_EMP_005);
    }
  }

  /**
   * Change the password of a user in Azure Graph.
   *
   * @param clientId the client ID of the user whose password is to be changed
   * @param request the request containing password change details
   */
  @Override
  public void changeUserPassword(String clientId, ChangePasswordRequest request) {
    try {
      PasswordProfile passwordProfile = new PasswordProfile();
      passwordProfile.setPassword(request.getNewPassword());
      passwordProfile.setForceChangePasswordNextSignIn(Boolean.FALSE);
      User user = new User();
      user.setPasswordProfile(passwordProfile);
      graphClient.users().byUserId(clientId).patch(user);
    } catch (Exception e) {
      log.error("Failed to change user password: {}", e.getMessage(), e);
      throw ExceptionFactory.badRequest(MessageConstant.E_EMP_006);
    }
  }
}
