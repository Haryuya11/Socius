package com.uit.sociuscoremodules.employee.service.impl;

import com.microsoft.graph.models.User;
import com.uit.sociuscoremodules.employee.dto.EmployeeDto;
import com.uit.sociuscoremodules.employee.repository.EmployeeRepository;
import com.uit.sociuscoremodules.employee.request.ChangePasswordRequest;
import com.uit.sociuscoremodules.employee.request.EmployeeCreateRequest;
import com.uit.sociuscoremodules.employee.service.AzureGraphService;
import com.uit.sociuscoremodules.employee.service.EmployeeService;
import com.uit.sociuscoremodules.shared.constants.MessageConstant;
import com.uit.sociuscoremodules.shared.security.UserContentProvider;
import com.uit.sociuscoremodules.shared.service.impl.BaseServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** Implementation of EmployeeService for employee-related operations. */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl extends BaseServiceImpl implements EmployeeService {

  /** Repository for accessing employee data. */
  private final EmployeeRepository employeeRepository;

  /** Provider for user content. */
  private final UserContentProvider userContentProvider;

  /** Service for interacting with Azure Graph API. */
  private final AzureGraphService azureGraphService;

  /**
   * Get the profile of the currently logged-in employee.
   *
   * @return EmployeeDto representing the employee profile
   */
  @Override
  public EmployeeDto employeeProfile() {
    return userContentProvider.getUserContent();
  }

  /**
   * Create a new user profile.
   *
   * @param request the request containing user creation details
   */
  @Override
  public void createUserProfile(EmployeeCreateRequest request) {

    EmployeeDto deletedUser = employeeRepository.findDeletedByUserId(request.getUserId());

    User user =
        (deletedUser == null)
            ? azureGraphService.createUser(request)
            : azureGraphService.reactivateUser(deletedUser.getClientId(), request);

    request.setClientId(user.getId());

    if (deletedUser == null) {
      employeeRepository.create(request);
    } else {
      employeeRepository.update(request);
    }
  }

  @Override
  public void updateUserProfile(EmployeeCreateRequest request, String clientId) {
    EmployeeDto user = employeeRepository.findByClientId(clientId);

    if (user == null) {
      throw notFound(MessageConstant.W_EMP_002);
    }
    azureGraphService.updateUser(clientId, request);
    request.setClientId(clientId);
    employeeRepository.update(request);
  }

  @Override
  public void deactivateUserProfile(String clientId) {
    EmployeeDto user = employeeRepository.findByClientId(clientId);

    if (user == null) {
      throw notFound(MessageConstant.W_EMP_002);
    }
    azureGraphService.deactivateUser(clientId);
    employeeRepository.deactivate(clientId);
  }

  /**
   * Change the password of a user.
   *
   * @param clientId the client ID of the user whose password is to be changed
   * @param request the request containing password change details
   */
  @Override
  public void changeUserPassword(String clientId, ChangePasswordRequest request) {
    if (!request.getNewPassword().equals(request.getConfirmPassword())) {
      log.info("New password and confirm password do not match for clientId: {}", clientId);
      throw badRequest(MessageConstant.E_EMP_006);
    }

    EmployeeDto user = employeeRepository.findByClientId(clientId);
    if (user == null) {
      throw notFound(MessageConstant.W_EMP_002);
    }
    azureGraphService.changeUserPassword(clientId, request);
  }
}
