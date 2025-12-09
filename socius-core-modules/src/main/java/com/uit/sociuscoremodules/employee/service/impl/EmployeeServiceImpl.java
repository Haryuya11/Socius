package com.uit.sociuscoremodules.employee.service.impl;

import com.uit.sociuscoremodules.employee.constants.EmployeeConstant;
import com.uit.sociuscoremodules.employee.dto.EmployeeDto;
import com.uit.sociuscoremodules.employee.dto.SearchEmployeeDto;
import com.uit.sociuscoremodules.employee.repository.EmployeeRepository;
import com.uit.sociuscoremodules.employee.request.ChangePasswordRequest;
import com.uit.sociuscoremodules.employee.request.EmployeeCreateRequest;
import com.uit.sociuscoremodules.employee.request.SearchUserRequest;
import com.uit.sociuscoremodules.employee.service.AzureGraphService;
import com.uit.sociuscoremodules.employee.service.EmployeeService;
import com.uit.sociuscoremodules.notification.converter.NotificationConverter;
import com.uit.sociuscoremodules.notification.domain.Notification;
import com.uit.sociuscoremodules.notification.dto.NotificationDto;
import com.uit.sociuscoremodules.notification.dto.PayloadDto;
import com.uit.sociuscoremodules.notification.repository.NotificationRepository;
import com.uit.sociuscoremodules.notification.request.NotificationCreateRequest;
import com.uit.sociuscoremodules.notification.service.NotificationPublisher;
import com.uit.sociuscoremodules.shared.constants.CommonConstant;
import com.uit.sociuscoremodules.shared.constants.MessageConstant;
import com.uit.sociuscoremodules.shared.request.PaginationSearchRequest;
import com.uit.sociuscoremodules.shared.response.PageResponse;
import com.uit.sociuscoremodules.shared.security.UserContentProvider;
import com.uit.sociuscoremodules.shared.service.impl.BaseServiceImpl;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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

  /** Repository for accessing notification data. */
  private final NotificationRepository notificationRepository;

  /** Converter for transforming notification data. */
  private final NotificationConverter notificationConverter;

  /** Provider for user content. */
  private final UserContentProvider userContentProvider;

  /** Service for interacting with Azure Graph API. */
  private final AzureGraphService azureGraphService;

  /** Publisher for sending notifications. */
  private final NotificationPublisher notificationPublisher;

  // ========================= EMPLOYEE SERVICE MAIN METHODS =========================
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
  public Map<String, String> createUserProfile(EmployeeCreateRequest request) {

    EmployeeDto existingUser = employeeRepository.findByUserId(request.getUserId());
    if (existingUser != null) {
      log.info("User with userId: {} already exists.", request.getUserId());
      throw badRequest(MessageConstant.E_EMP_007);
    }

    EmployeeDto deletedUser = employeeRepository.findDeletedByUserId(request.getUserId());

    String clientId =
        (deletedUser == null)
            ? azureGraphService.createUser(request).getId()
            : deletedUser.getClientId();

    request.setClientId(clientId);

    if (deletedUser == null) {
      employeeRepository.create(request);
    } else {
      azureGraphService.reactivateUser(clientId, request);
      employeeRepository.reactivate(request);
    }

    sendNotification(
        userContentProvider.getUserContent().getClientId(),
        "Account Created",
        String.format(
            "Account for %s %s has been created.", request.getLastName(), request.getFirstName()),
        null);

    return Map.of(EmployeeConstant.CLIENT_ID, clientId);
  }

  /**
   * Update an existing user profile.
   *
   * @param request the request containing user update details
   * @param clientId the client ID of the user to be updated
   */
  @Override
  public void updateUserProfile(EmployeeCreateRequest request, String clientId) {
    EmployeeDto user = employeeRepository.findByClientId(clientId);

    if (user == null) {
      throw notFound(MessageConstant.W_EMP_002);
    }
    azureGraphService.updateUser(clientId, request);
    request.setClientId(clientId);
    employeeRepository.update(request);
    sendNotification(
        clientId, "Account Updated", "Your account information has been updated.", null);
  }

  /**
   * Deactivate a user profile.
   *
   * @param clientId the client ID of the user to be deactivated
   */
  @Override
  public void deactivateUserProfile(String clientId) {
    EmployeeDto user = employeeRepository.findByClientId(clientId);

    if (user == null) {
      throw notFound(MessageConstant.W_EMP_002);
    }
    azureGraphService.deactivateUser(clientId);
    employeeRepository.deactivate(clientId);

    sendNotification(
        userContentProvider.getUserContent().getClientId(),
        "Account Deactivated",
        String.format(
            "Account for %s %s has been deactivated.", user.getLastName(), user.getFirstName()),
        null);
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

  // ========================= NOTIFICATION HELPERS =========================

  /**
   * Send a notification to a user.
   *
   * @param receiverId the ID of the notification receiver
   * @param title the title of the notification
   * @param content the content of the notification
   * @param linkUrl the link URL of the notification
   */
  private void sendNotification(String receiverId, String title, String content, String linkUrl) {
    LocalDateTime dateTime = LocalDateTime.now();

    PayloadDto payloadDto = buildPayload(title, content, linkUrl);

    NotificationCreateRequest request = buildNotificationRequest(receiverId, payloadDto);

    Notification notification = notificationConverter.fromCreateRequest(request, dateTime);

    notificationRepository.insert(notification);

    NotificationDto dto = notificationConverter.toDto(notification);

    notificationPublisher.publishNotification(dto);
  }

  // ========================= HELPER BUILD METHODS =========================
  /**
   * Build a notification object.
   *
   * @param receiverId the ID of the notification receiver
   * @param payloadDto the payload of the notification
   * @return the constructed Notification object
   */
  private NotificationCreateRequest buildNotificationRequest(
      String receiverId, PayloadDto payloadDto) {
    return NotificationCreateRequest.builder().receiverId(receiverId).payload(payloadDto).build();
  }

  /**
   * Build a payload object.
   *
   * @param title the title of the payload
   * @param content the content of the payload
   * @param linkUrl the link URL of the payload
   * @return the constructed Payload object
   */
  private PayloadDto buildPayload(String title, String content, String linkUrl) {
    return PayloadDto.builder().title(title).content(content).linkUrl(linkUrl).build();
  }

  /**
   * Search for employees based on given criteria with pagination.
   *
   * @param request the pagination search request containing search criteria
   * @return a paginated response of EmployeeDto matching the search criteria
   */
  @Override
  public PageResponse<SearchEmployeeDto> search(
      PaginationSearchRequest<SearchUserRequest> request) {
    SearchUserRequest criteria = request.getCondition();

    int total = employeeRepository.count(criteria);
    if (total == CommonConstant.INIT_INDEX) {
      log.info("No employees found matching the search criteria.");
      return PageResponse.empty();
    }

    int limit = request.getPageRequest().getPageSize();
    int offset = (request.getPageRequest().getPageNumber() - 1) * limit;
    List<SearchEmployeeDto> result =
        employeeRepository.search(criteria, request.getSortRequests(), limit, offset);
    return PageResponse.of(result, total, offset, limit);
  }

  /**
   * Find an employee by ID.
   *
   * @param id the employee ID
   * @return the corresponding EmployeeDto
   */
  @Override
  public EmployeeDto findById(String id) {
    EmployeeDto employee = employeeRepository.findByClientId(id);
    if (employee == null) {
      throw notFound(MessageConstant.W_EMP_002);
    }
    return employee;
  }
}
