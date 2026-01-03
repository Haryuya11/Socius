package com.uit.sociusmvcapp.employee.internal.adapter;

import com.microsoft.graph.models.User;
import com.uit.sociusmvcapp.azure.graph.AzureGraphProperties;
import com.uit.sociusmvcapp.azure.graph.AzureGraphService;
import com.uit.sociusmvcapp.azure.graph.ChangePasswordRequest;
import com.uit.sociusmvcapp.employee.dto.request.CreateEmployeeRequest;
import com.uit.sociusmvcapp.employee.internal.converter.EmployeeConverter;
import com.uit.sociusmvcapp.shared.constants.MessageConstant;
import com.uit.sociusmvcapp.shared.exception.BusinessException;
import com.uit.sociusmvcapp.shared.service.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Adapter for Azure Graph Service operations. This class handles conversion between domain models
 * and Azure Graph User objects, and hides Azure Graph configuration details from the service layer.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EmployeeGraphAdapter {

  private final AzureGraphService azureGraphService;
  private final EmployeeConverter employeeConverter;

  @Value("${azure.graph.tenant-id}")
  private String tenantId;

  @Value("${azure.graph.client-id}")
  private String clientId;

  @Value("${azure.graph.client-secret}")
  private String clientSecret;

  @Value("${azure.graph.scope}")
  private String scope;

  @Value("${azure.graph.domain-name}")
  private String domainName;

  /**
   * Build AzureGraphProperties from injected configuration values.
   *
   * @return AzureGraphProperties instance
   */
  private AzureGraphProperties buildProperties() {
    return AzureGraphProperties.builder()
        .tenantId(tenantId)
        .clientId(clientId)
        .clientSecret(clientSecret)
        .scope(scope)
        .domainName(domainName)
        .build();
  }

  /**
   * Create a new user in Azure Active Directory.
   *
   * @param request the request containing user creation details
   * @return the created User object
   */
  public User createUser(CreateEmployeeRequest request) {
    AzureGraphProperties properties = buildProperties();
    User userToCreate = employeeConverter.toGraphUser(request, properties.getDomainName());
    return azureGraphService.createUser(properties, userToCreate);
  }

  /**
   * Update an existing user in Azure Active Directory.
   *
   * @param clientId the client ID of the user to update
   * @param request the request containing user update details
   */
  public void updateUser(String clientId, CreateEmployeeRequest request) {
    AzureGraphProperties properties = buildProperties();
    User userToUpdate = employeeConverter.toGraphUserForUpdate(request);
    azureGraphService.updateUser(properties, clientId, userToUpdate);
  }

  /**
   * Deactivate a user in Azure Active Directory.
   *
   * @param clientId the client ID of the user to deactivate
   */
  public void deactivateUser(String clientId) {
    AzureGraphProperties properties = buildProperties();
    azureGraphService.deactivateUser(properties, clientId);
  }

  /**
   * Find a user by their client ID in Azure Active Directory.
   *
   * @param clientId the client ID of the user to find
   * @return the User object
   */
  public User findById(String clientId) {
    AzureGraphProperties properties = buildProperties();
    return azureGraphService.findById(properties, clientId);
  }

  /**
   * Reactivate a user in Azure Active Directory.
   *
   * @param clientId the client ID of the user to reactivate
   * @param request the user creation request containing updated information
   */
  public void reactivateUser(String clientId, CreateEmployeeRequest request) {
    AzureGraphProperties properties = buildProperties();
    User userToReactivate = employeeConverter.toGraphUserForUpdate(request);
    azureGraphService.reactivateUser(properties, clientId, userToReactivate);
  }

  /**
   * Change the password of a user in Azure Active Directory using On-Behalf-Of flow.
   *
   * @param clientId the client ID of the user whose password is to be changed
   * @param request the request containing current and new password
   */
  public void changeUserPassword(String clientId, ChangePasswordRequest request) {
    AzureGraphProperties properties = buildProperties();
    azureGraphService.changeUserPassword(properties, clientId, request);
  }

  /**
   * Validate that a user exists by their client ID.
   *
   * @param clientId the client ID of the user to validate
   * @throws BusinessException if the user does not exist
   */
  public void validateUserExists(String clientId) {
    if (this.findById(clientId) == null) {
      throw ExceptionFactory.notFound(MessageConstant.W_EMP_002);
    }
  }
}
