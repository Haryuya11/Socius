package com.uit.sociusmvcapp.azure.graph.internal.service;

import com.microsoft.graph.models.PasswordProfile;
import com.microsoft.graph.models.User;
import com.microsoft.graph.serviceclient.GraphServiceClient;
import com.uit.sociusmvcapp.azure.graph.AzureGraphProperties;
import com.uit.sociusmvcapp.azure.graph.AzureGraphService;
import com.uit.sociusmvcapp.azure.graph.ChangePasswordRequest;
import com.uit.sociusmvcapp.azure.graph.internal.constants.GraphUserFields;
import com.uit.sociusmvcapp.azure.graph.internal.domain.GraphClientProperties;
import com.uit.sociusmvcapp.azure.graph.internal.factory.GraphClientFactory;
import com.uit.sociusmvcapp.shared.constants.MessageConstant;
import com.uit.sociusmvcapp.shared.service.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** Implementation of AzureGraphService for interacting with Microsoft Graph API. */
@Service
@RequiredArgsConstructor
@Slf4j
public class AzureGraphServiceImpl implements AzureGraphService {

  private final GraphClientFactory graphClientFactory;

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

  /**
   * Create a new user in Azure Active Directory.
   *
   * @param properties Azure Graph configuration properties
   * @param user the User object containing user creation details
   * @return the created User object
   */
  @Override
  public User createUser(AzureGraphProperties properties, User user) {
    try {
      log.info(
          "Creating new user with principalName={}, domain={}",
          user.getUserPrincipalName(),
          properties.getDomainName());

      GraphServiceClient graphClient = createGraphClient(properties);
      User createdUser = graphClient.users().post(user);

      log.info("Successfully created user with ID={}", createdUser.getId());
      return createdUser;
    } catch (Exception e) {
      log.error("Failed to create user: {}", e.getMessage(), e);
      throw ExceptionFactory.badRequest(MessageConstant.E_EMP_001);
    }
  }

  /**
   * Update an existing user in Azure Active Directory.
   *
   * @param properties Azure Graph configuration properties
   * @param clientId the client ID of the user to update
   * @param user the request containing user update details
   */
  @Override
  public void updateUser(AzureGraphProperties properties, String clientId, User user) {
    try {
      log.info("Updating user with clientId={}", clientId);

      GraphServiceClient graphClient = createGraphClient(properties);
      graphClient.users().byUserId(clientId).patch(user);

      log.info("Successfully updated user with clientId={}", clientId);
    } catch (Exception e) {
      log.error("Failed to update user with clientId={}: {}", clientId, e.getMessage(), e);
      throw ExceptionFactory.badRequest(MessageConstant.E_EMP_002);
    }
  }

  /**
   * Deactivate a user in Azure Active Directory.
   *
   * @param properties Azure Graph configuration properties
   * @param clientId the client ID of the user to deactivate
   */
  @Override
  public void deactivateUser(AzureGraphProperties properties, String clientId) {
    try {
      log.info("Deactivating user with clientId={}", clientId);

      User user = findById(properties, clientId);
      if (user != null && !Boolean.FALSE.equals(user.getAccountEnabled())) {
        user.setAccountEnabled(false);

        GraphServiceClient graphClient = createGraphClient(properties);
        graphClient.users().byUserId(clientId).patch(user);

        log.info("Successfully deactivated user with clientId={}", clientId);
      } else {
        log.info("User with clientId={} is already deactivated or not found", clientId);
      }
    } catch (Exception e) {
      log.error("Failed to deactivate user with clientId={}: {}", clientId, e.getMessage(), e);
      throw ExceptionFactory.badRequest(MessageConstant.E_EMP_003);
    }
  }

  /**
   * Find a user by their client ID in Azure Active Directory.
   *
   * @param properties Azure Graph configuration properties
   * @param clientId the client ID of the user to find
   * @return the User object
   */
  @Override
  public User findById(AzureGraphProperties properties, String clientId) {
    try {
      log.debug("Finding user with clientId={}", clientId);

      GraphServiceClient graphClient = createGraphClient(properties);
      User user =
          graphClient
              .users()
              .byUserId(clientId)
              .get(
                  requestConfig -> {
                    assert requestConfig.queryParameters != null;
                    requestConfig.queryParameters.select = DEFAULT_USER_FIELDS;
                  });

      log.debug("Successfully found user with clientId={}", clientId);
      return user;
    } catch (Exception e) {
      log.error("Failed to find user with clientId={}: {}", clientId, e.getMessage(), e);
      throw ExceptionFactory.badRequest(MessageConstant.E_EMP_004);
    }
  }

  /**
   * Reactivate a user in Azure Active Directory.
   *
   * @param properties Azure Graph configuration properties
   * @param clientId the client ID of the user to reactivate
   * @param user the User object containing updated user details
   */
  @Override
  public void reactivateUser(AzureGraphProperties properties, String clientId, User user) {
    try {
      log.info("Reactivating user with clientId={}", clientId);

      User existingUser = findById(properties, clientId);
      user.setId(existingUser.getId());
      user.setAccountEnabled(true);

      GraphServiceClient graphClient = createGraphClient(properties);
      graphClient.users().byUserId(clientId).patch(user);

      log.info("Successfully reactivated user with clientId={}", clientId);
    } catch (Exception e) {
      log.error("Failed to reactivate user with clientId={}: {}", clientId, e.getMessage(), e);
      throw ExceptionFactory.badRequest(MessageConstant.E_EMP_005);
    }
  }

  /**
   * Change the password of a user in Azure Active Directory using On-Behalf-Of flow.
   *
   * @param properties Azure Graph configuration properties
   * @param clientId the client ID of the user whose password is to be changed
   * @param request the request containing current and new password
   */
  @Override
  public void changeUserPassword(
      AzureGraphProperties properties, String clientId, ChangePasswordRequest request) {
    try {
      log.info("Initiating password change for user");
      PasswordProfile passwordProfile = new PasswordProfile();
      passwordProfile.setPassword(request.getNewPassword());
      passwordProfile.setForceChangePasswordNextSignIn(Boolean.FALSE);
      User user = new User();
      user.setPasswordProfile(passwordProfile);
      GraphServiceClient graphClient = createGraphClient(properties);
      graphClient.users().byUserId(clientId).patch(user);
      log.info("Successfully changed user password");
    } catch (Exception e) {
      log.error("Failed to change user password: {}", e.getMessage(), e);
      throw ExceptionFactory.badRequest(MessageConstant.E_EMP_006);
    }
  }

  /**
   * Create a GraphServiceClient with client credentials flow.
   *
   * @param properties Azure Graph configuration properties
   * @return GraphServiceClient instance
   */
  private GraphServiceClient createGraphClient(AzureGraphProperties properties) {
    GraphClientProperties clientProperties =
        GraphClientProperties.builder()
            .tenantId(properties.getTenantId())
            .clientId(properties.getClientId())
            .clientSecret(properties.getClientSecret())
            .scope(properties.getScope())
            .build();

    return graphClientFactory.createClientCredentialClient(clientProperties);
  }
}
