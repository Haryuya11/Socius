package com.uit.sociuscoremodules.employee.service;

import com.microsoft.graph.models.User;
import com.uit.sociuscoremodules.employee.request.ChangePasswordRequest;
import com.uit.sociuscoremodules.employee.request.EmployeeCreateRequest;

/** Service interface for interacting with Azure Graph API for user management. */
public interface AzureGraphService {
  /**
   * Create a new user in Azure Graph.
   *
   * @param request the user creation request
   * @return the created User object
   */
  User createUser(EmployeeCreateRequest request);

  /**
   * Update an existing user in Azure Graph.
   *
   * @param clientId the client ID of the user to update
   * @param request the user creation request
   * @return the updated User object
   */
  User updateUser(String clientId, EmployeeCreateRequest request);

  /**
   * Deactivate a user in Azure Graph.
   *
   * @param clientId the client ID of the user to deactivate
   */
  void deactivateUser(String clientId);

  /**
   * Find a user by client ID in Azure Graph.
   *
   * @param clientId the client ID of the user to find
   * @return the User object
   */
  User findById(String clientId);

  /**
   * Reactivate a user in Azure Graph.
   *
   * @param clientId the client ID of the user to reactivate
   * @param request the user creation request
   * @return the reactivated User object
   */
  User reactivateUser(String clientId, EmployeeCreateRequest request);

  /**
   * Change the password of a user in Azure Graph.
   *
   * @param clientId the client ID of the user whose password is to be changed
   * @param request the request containing password change details
   */
  void changeUserPassword(String clientId, ChangePasswordRequest request);
}
