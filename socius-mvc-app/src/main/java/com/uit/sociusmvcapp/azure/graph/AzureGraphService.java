package com.uit.sociusmvcapp.azure.graph;

import com.microsoft.graph.models.User;

/** Service interface for Azure Graph API operations. */
public interface AzureGraphService {

  /**
   * Create a new user in Azure Active Directory.
   *
   * @param properties Azure Graph configuration properties
   * @param user the User object containing user creation details
   * @return the created User object
   */
  User createUser(AzureGraphProperties properties, User user);

  /**
   * Update an existing user in Azure Active Directory.
   *
   * @param properties Azure Graph configuration properties
   * @param clientId the client ID of the user to update
   * @param user the User object containing updated user details
   */
  void updateUser(AzureGraphProperties properties, String clientId, User user);

  /**
   * Deactivate a user in Azure Active Directory.
   *
   * @param properties Azure Graph configuration properties
   * @param clientId the client ID of the user to deactivate
   */
  void deactivateUser(AzureGraphProperties properties, String clientId);

  /**
   * Find a user by their client ID in Azure Active Directory.
   *
   * @param properties Azure Graph configuration properties
   * @param clientId the client ID of the user to find
   * @return the User object
   */
  User findById(AzureGraphProperties properties, String clientId);

  /**
   * Reactivate a user in Azure Active Directory.
   *
   * @param properties Azure Graph configuration properties
   * @param clientId the client ID of the user to reactivate
   * @param user the User object containing updated user details
   */
  void reactivateUser(AzureGraphProperties properties, String clientId, User user);

  /**
   * Change the password of a user in Azure Active Directory using On-Behalf-Of flow.
   *
   * @param properties Azure Graph configuration properties
   * @param clientId the client ID of the user whose password is to be changed
   * @param request the request containing current and new password
   */
  void changeUserPassword(
      AzureGraphProperties properties, String clientId, ChangePasswordRequest request);
}
