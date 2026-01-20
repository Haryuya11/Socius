package com.uit.sociusmvcapp.employee.internal.service;

import com.microsoft.graph.models.User;
import com.uit.sociusmvcapp.azure.blob.UploadFileDto;
import com.uit.sociusmvcapp.azure.graph.ChangePasswordRequest;
import com.uit.sociusmvcapp.employee.EmployeeDeletedEvent;
import com.uit.sociusmvcapp.employee.EmployeeService;
import com.uit.sociusmvcapp.employee.WorkforceGateway;
import com.uit.sociusmvcapp.employee.dto.EmployeeDto;
import com.uit.sociusmvcapp.employee.dto.SearchEmployeeDto;
import com.uit.sociusmvcapp.employee.dto.request.CreateEmployeeRequest;
import com.uit.sociusmvcapp.employee.dto.request.SearchUserRequest;
import com.uit.sociusmvcapp.employee.internal.adapter.EmployeeBlobAdapter;
import com.uit.sociusmvcapp.employee.internal.adapter.EmployeeGraphAdapter;
import com.uit.sociusmvcapp.employee.internal.constants.EmployeeConstant;
import com.uit.sociusmvcapp.employee.internal.repository.EmployeeRepository;
import com.uit.sociusmvcapp.iam.UserContentProvider;
import com.uit.sociusmvcapp.iam.dto.UserDepartmentInfo;
import com.uit.sociusmvcapp.iam.dto.UserPrincipal;
import com.uit.sociusmvcapp.iam.dto.UserTeamInfo;
import com.uit.sociusmvcapp.shared.constants.CommonConstant;
import com.uit.sociusmvcapp.shared.constants.MessageConstant;
import com.uit.sociusmvcapp.shared.event.NotificationSendEvent;
import com.uit.sociusmvcapp.shared.request.PaginationSearchRequest;
import com.uit.sociusmvcapp.shared.response.PageResponse;
import com.uit.sociusmvcapp.shared.service.ExceptionFactory;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/** Implementation of EmployeeService for employee-related operations. */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

  /** Repository for accessing employee data. */
  private final EmployeeRepository employeeRepository;

  /** Provider for user content. */
  private final UserContentProvider userContentProvider;

  /** Service for sending notifications. */
  private final ApplicationEventPublisher eventPublisher;

  /** Gateway for workforce-related data. */
  private final WorkforceGateway workforceGateway;

  /** Adapter for Azure Graph operations. */
  private final EmployeeGraphAdapter employeeGraphAdapter;

  /** Adapter for Azure Blob operations specific to Employee module. */
  private final EmployeeBlobAdapter employeeBlobAdapter;

  // ========================= EMPLOYEE SERVICE MAIN METHODS =========================
  /**
   * Get the profile of the currently logged-in employee.
   *
   * @return EmployeeDto representing the employee profile
   */
  @Override
  public UserPrincipal employeeProfile() {
    return userContentProvider.getUserContent();
  }

  /**
   * Create a new user profile.
   *
   * @param request the request containing user creation details
   */
  @Override
  @Transactional
  public Map<String, String> create(CreateEmployeeRequest request) {
    EmployeeDto existingUser = employeeRepository.findByUserId(request.getUserId());
    if (existingUser != null) {
      throw ExceptionFactory.badRequest(MessageConstant.E_EMP_007);
    }

    EmployeeDto deletedUser = employeeRepository.findDeletedByUserId(request.getUserId());
    String userClientId;

    if (deletedUser == null) {
      User azureUser = employeeGraphAdapter.createUser(request);
      if (azureUser == null || azureUser.getId() == null) {
        log.error("Failed to create user on Azure: Azure returned null");
        throw ExceptionFactory.internalError(MessageConstant.E_SYS_001);
      }

      userClientId = azureUser.getId();
      request.setClientId(userClientId);
      employeeRepository.create(request);
    } else {
      userClientId = deletedUser.getClientId();

      if (userClientId == null || userClientId.isEmpty()) {
        log.error(
            "Deleted user found but clientId is missing in DB for userId: {}", request.getUserId());
        throw ExceptionFactory.internalError(MessageConstant.E_SYS_001);
      }

      request.setClientId(userClientId);
      employeeGraphAdapter.reactivateUser(userClientId, request);
      employeeRepository.reactivate(request);
    }

    eventPublisher.publishEvent(
        new NotificationSendEvent(
            this,
            userContentProvider.getUserContent().getClientId(),
            "Account Created",
            String.format(
                "Account for %s %s has been created.",
                request.getLastName(), request.getFirstName()),
            "/employees/" + userClientId));

    return Map.of(EmployeeConstant.CLIENT_ID, userClientId);
  }

  /**
   * Update an existing user profile.
   *
   * @param request the request containing user update details
   * @param clientId the client ID of the user to be updated
   */
  @Override
  @Transactional
  public void update(CreateEmployeeRequest request, String clientId) {
    EmployeeDto user = employeeRepository.findByClientId(clientId);
    if (user == null) {
      throw ExceptionFactory.notFound(MessageConstant.W_EMP_002);
    }
    employeeGraphAdapter.updateUser(clientId, request);
    request.setClientId(clientId);
    employeeRepository.update(request);
    eventPublisher.publishEvent(
        new NotificationSendEvent(
            this,
            userContentProvider.getUserContent().getClientId(),
            "Account Updated",
            "Your account information has been updated.",
            "/profile"));
  }

  /**
   * Deactivate a user profile.
   *
   * @param clientId the client ID of the user to be deactivated
   */
  @Override
  @Transactional
  public void deactivate(String clientId) {
    EmployeeDto user = employeeRepository.findByClientId(clientId);

    if (user == null) {
      throw ExceptionFactory.notFound(MessageConstant.W_EMP_002);
    }
    employeeGraphAdapter.deactivateUser(clientId);
    employeeRepository.deactivate(clientId);

    eventPublisher.publishEvent(new EmployeeDeletedEvent(clientId));

    eventPublisher.publishEvent(
        new NotificationSendEvent(
            this,
            userContentProvider.getUserContent().getClientId(),
            "Account Deactivated",
            String.format(
                "Account for %s %s has been deactivated.", user.getLastName(), user.getFirstName()),
            "/employees"));
  }

  /**
   * Change the password of a user.
   *
   * @param request the request containing password change details
   */
  @Override
  public void changeUserPassword(ChangePasswordRequest request) {
    String userClientId = userContentProvider.getUserContent().getClientId();
    if (!request.getNewPassword().equals(request.getConfirmPassword())) {
      throw ExceptionFactory.badRequest(MessageConstant.E_EMP_006);
    }
    employeeGraphAdapter.changeUserPassword(userClientId, request);
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
    int limit = request.getPageRequest().getPageSize();
    int offset = (request.getPageRequest().getPageNumber() - 1) * limit;
    SearchUserRequest criteria = request.getCondition();

    int total = employeeRepository.count(criteria);
    if (total == CommonConstant.INIT_INDEX) {
      log.info("No employees found matching the search criteria.");
      return PageResponse.empty();
    }

    List<SearchEmployeeDto> result =
        employeeRepository.search(criteria, request.getSortRequests(), limit, offset);

    if (!result.isEmpty()) {
      enrichWithWorkforceData(result);
    }

    return PageResponse.of(result, total, offset, limit);
  }

  /**
   * Find an employee by ID.
   *
   * @param id the employee ID
   * @return the corresponding EmployeeDto
   */
  @Override
  public EmployeeDto findByClientId(String id) {
    EmployeeDto employee = employeeRepository.findByClientId(id);
    if (employee == null) {
      throw ExceptionFactory.notFound(MessageConstant.W_EMP_002);
    }
    return employee;
  }

  /**
   * Find multiple employees by their client IDs in a single batch query.
   *
   * @param clientIds list of employee client IDs
   * @return list of EmployeeDto matching the provided client IDs
   */
  @Override
  public List<EmployeeDto> findByClientIds(List<String> clientIds) {
    if (clientIds == null || clientIds.isEmpty()) {
      return List.of();
    }
    return employeeRepository.findByClientIds(clientIds);
  }

  /**
   * Validate if an employee exists by client ID.
   *
   * @param clientId the employee client ID
   */
  @Override
  public void validateExists(String clientId) {
    if (!employeeRepository.existsByClientId(clientId)) {
      throw ExceptionFactory.notFound(MessageConstant.W_EMP_002);
    }

    employeeGraphAdapter.validateUserExists(clientId);
  }

  /**
   * Upload an avatar file for the employee.
   *
   * @param file the avatar file to be uploaded
   * @return UploadFileDto containing details of the uploaded file
   */
  @Override
  public UploadFileDto uploadAvatar(MultipartFile file) {
    String userClientId = userContentProvider.getUserContent().getClientId();
    return employeeBlobAdapter.uploadAvatar(file, userClientId);
  }

  /**
   * Get the full URL of an avatar given its path.
   *
   * @param path the path of the avatar
   * @return the full URL of the avatar
   */
  @Override
  public String getAvatarUrl(String path) {
    return employeeBlobAdapter.getAvatarUrl(path);
  }

  /**
   * Enrich a list of SearchEmployeeDto with workforce data (departments and teams).
   *
   * @param dtos the list of SearchEmployeeDto to be enriched
   */
  private void enrichWithWorkforceData(List<SearchEmployeeDto> dtos) {
    List<String> empIds = dtos.stream().map(SearchEmployeeDto::getClientId).toList();

    // B. Gọi Gateway (Batch call - chỉ tốn 2 query SQL)
    var deptMap = workforceGateway.getDepartmentsByEmployeeIds(empIds);
    var teamMap = workforceGateway.getTeamsByEmployeeIds(empIds);

    // C. Map dữ liệu vào DTO
    for (SearchEmployeeDto dto : dtos) {
      String empId = dto.getClientId();

      // --- Map Departments ---
      List<UserDepartmentInfo> iamDepts = deptMap.getOrDefault(empId, Collections.emptyList());
      dto.setDepartments(
          iamDepts.stream()
              .map(
                  iam ->
                      new SearchEmployeeDto.DepartmentInfo(
                          iam.getDepartmentCode(),
                          iam.getDepartmentName(),
                          iam.getRoleCode(),
                          iam.getIsPrimary()))
              .toList());

      // --- Map Teams ---
      List<UserTeamInfo> iamTeams = teamMap.getOrDefault(empId, Collections.emptyList());
      dto.setTeams(
          iamTeams.stream()
              .map(
                  iam ->
                      new SearchEmployeeDto.TeamInfo(
                          iam.getTeamCode(),
                          iam.getTeamName(),
                          iam.getRoleCode(),
                          iam.getIsLeader()))
              .toList());
    }
  }
}
